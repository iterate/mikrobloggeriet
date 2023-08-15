(ns mikrobloggeriet.serve
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.pprint]
   [clojure.string :as str]
   [compojure.core :refer [defroutes GET]]
   [hiccup.page :as page]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.jals :as jals]
   [mikrobloggeriet.olorm :as olorm]
   [mikrobloggeriet.pandoc :as pandoc]
   [org.httpkit.server :as httpkit]
   [ring.middleware.cookies :as cookies]
   [clojure.pprint :as pprint]))

(defn shared-html-header
  "Shared HTML, including CSS.

  Handles CSS theming system with cookies."
  [req]
  [[:meta {:charset "utf-8"}]
   (hiccup.page/include-css "/vanilla.css")
   (hiccup.page/include-css "/mikrobloggeriet.css")
   (let [theme (get-in (cookies/cookies-request req)
                       [:cookies "theme" :value]
                       "vanilla")]
     (hiccup.page/include-css (str "/theme/" theme ".css")))])

(defn feeling-lucky []
  [:a {:href "/random-doc" :class :feeling-lucky} "🎲"])

(defn repo-path [] ".")

(defn set-theme [req]
  (tap> req) 
  (let [target "/"
        theme (or (:theme (:route-params req)) "")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target
               "Set-Cookie" (str "theme=" theme "; Path=/")}
     :body ""}))

(defn index [req]
  (tap> req)
  (let [mikrobloggeriet-announce-url "https://garasjen.slack.com/archives/C05355N5TCL"
        github-mikrobloggeriet-url "https://github.com/iterate/mikrobloggeriet/"
        _tech-forum-url "https://garasjen.slack.com/archives/C2K35RDNJ"
        teodor-url "https://teod.eu/"
        hops-url "https://www.headless-operations.no/"
        iterate-url "https://www.iterate.no/"]
    {:status 200
     :body
     (page/html5
      (into [:head] (shared-html-header req))
      [:body
       [:p (feeling-lucky)]
       [:h1 "Mikrobloggeriet"]
       [:p "Teknologer fra Iterate deler fra hverdagen."]
       #_
       (let [theme "solarized-dark"]
         [:a {:href (str "/set-theme/" theme)} (str "Set theme: " theme)])
       [:h2 "OLORM"]
       [:p "Mikrobloggen OLORM skrives av Oddmund, Lars og Richard."]
       [:p
        (interpose " · "
                   (for [olorm (olorm/docs {:repo-path (repo-path)})]
                     [:a {:href (olorm/href olorm)} (:slug olorm)]))]
       [:h2 "JALS"]
       [:p "Mikrobloggen JALS skrives av Adrian, Lars og Sindre. Jørgen har skrevet tidligere."]
       [:p
        (interpose " · "
                   (for [doc (jals/docs {:repo-path (repo-path)})]
                     [:a {:href (jals/href doc)} (:slug doc)]))]

       [:hr]

       [:section
        [:h2 "Hva er dette for noe?"]
        [:p
         "Mikrobloggeriet er et initiativ der teknologer fra " [:a {:href iterate-url} "Iterate"] " deler ting de bryr seg om i hverdagen. "
         "Vi velger å publisere fritt tilgjengelig på Internett fordi vi har tro på å dele kunnskap. "
         "Innhold og kode for Mikrobloggeriet på " [:a {:href github-mikrobloggeriet-url} "github.com/iterate/mikrobloggeriet"] ". "
         "Mikrobloggeriet kjører på " [:a {:href hops-url} "Headless Operations"] ". "
         "Hvis du har spørsmål eller kommentarer, kan du ta kontakt med " [:a {:href teodor-url} "Teodor"] "."]]

       [:section
        [:h2 "Er det mulig å diskutere publiserte dokumenter?"]
        [:p "Vi oppfordrer alle til å kommentere og diskutere!"
         " Men vi tror det er lettest å gjøre på Slack."
         " Delta gjerne i diskusjonen i tråd på "
         [:a {:href mikrobloggeriet-announce-url} "#mikrobloggeriet-announce"]
         "!"]]

       [:section
        [:h2 "Jeg er Iterate-teknolog og vil skrive, hva gjør jeg?"]
        [:p "Finn deg 2-3 andre å skrive med, og snakk med Teodor."
         " Vi setter av en time der vi går gjennom skriveprosessen og installerer tooling."
         " Deretter får dere en \"prøveuke\" der dere kan prøve dere på å skrive cirka hver tredje dag."
         " Så kan dere bestemme dere for om dere vil fortsette å skrive eller ikke."]
        ]
       [:hr]
       [:section
        (let [themes (->> (fs/list-dir "theme")
                          (map fs/file-name)
                          (map #(str/replace % #".css$" ""))
                          sort)]
          [:p "Sett tema: "
           (into [:span]
                 (interpose " | "
                            (for [t themes]
                              [:a {:href (str "/set-theme/" t)} t])))])]
       ])}))

(defn olorm-index [req]
  (page/html5
   (into [:head] (shared-html-header req))
   [:body
    [:p
     (feeling-lucky)
     " — "
     [:a {:href "/"} "mikrobloggeriet"]]
    [:h1 "Alle OLORM-er"]
    [:table
     [:thead [:td "slug"] [:td "author"] [:td "created"]]
     [:tbody
      (for [olorm (map olorm/load-meta (olorm/docs {:repo-path (repo-path)}))]
        [:tr
         [:td [:a {:href (olorm/href olorm)} (:slug olorm)]]
         [:td (olorm/author-name olorm)]
         [:td (:doc/created olorm)]])]]]))

(defn jals-index [req]
  (page/html5
   (into [:head] (shared-html-header req))
   [:body
    [:p
     (feeling-lucky)
     " — "
     [:a {:href "/"} "mikrobloggeriet"]]
    [:h1 "Alle JALS-er"]
    [:table
     [:thead [:td "slug"] [:td "author"] [:td "created"]]
     [:tbody
      (for [jals (map jals/load-meta (jals/docs {:repo-path (repo-path)}))]
        [:tr
         [:td [:a {:href (jals/href jals)} (:slug jals)]]
         [:td (jals/author-name jals)]
         [:td (:doc/created jals)]])]]]))

(def markdown->html+info
  (cache/cache-fn-by (fn markdown->html+info [markdown]
                        (let [pandoc (pandoc/from-markdown markdown)]
                          {:doc-html (pandoc/to-html pandoc)
                           :title (pandoc/infer-title pandoc)}))
                   identity))

(defn olorm [req]
  (tap> req)
  (let [olorm (olorm/->olorm {:slug (:slug (:route-params req))
                              :repo-path (repo-path)})
        {:keys [number]} olorm
        {:keys [doc-html title]}
        (when (olorm/exists? olorm)
          (markdown->html+info (slurp (olorm/index-md-path olorm))))]
    {:status (if (and olorm (olorm/exists? olorm)) 200 404)
     :body
     (page/html5
      (into [:head] (concat (shared-html-header req)
                            (when title
                              [[:title title]])))
      [:body
       [:p
        (feeling-lucky)
        " — "
        [:a {:href "/"} "mikrobloggeriet"]
        " "
        [:a {:href "/o/"} "o"]
        " — "
        [:span (interpose " · " (filter some?
                                        [(let [prev (olorm/->olorm {:number (dec number) :repo-path (repo-path)})]
                                           (when (olorm/exists? prev)
                                             [:a {:href (olorm/href prev)} (:slug prev)]))
                                         [:span (:slug olorm)]
                                         (let [prev (olorm/->olorm {:number (inc number) :repo-path (repo-path)})]
                                           (when (olorm/exists? prev)
                                             [:a {:href (olorm/href prev)} (:slug prev)]))]))]]
       doc-html])}))

(defn jals [req]
  (let [doc (jals/->doc {:slug (:slug (:route-params req))
                         :repo-path (repo-path)})
        {:keys [number]} doc
        {:keys [doc-html title]}
        (when (jals/exists? doc)
          (markdown->html+info (slurp (jals/index-md-path doc))))]
    {:status (if doc-html 200 404)
     :body
     (page/html5
      (into [:head] (concat (shared-html-header req)
                            (when title
                              [[:title title]])))
      [:body
       [:p (feeling-lucky)
        " — "
        [:a {:href "/"} "mikrobloggeriet"]
        " "
        [:a {:href "/j/"} "j"]
        " — "
        [:span (interpose " · " (filter some?
                                        [(let [prev (jals/->doc {:number (dec number) :repo-path (repo-path)})]
                                           (when (jals/exists? prev)
                                             [:a {:href (jals/href prev)} (:slug prev)]))
                                         [:span (:slug doc)]
                                         (let [prev (jals/->doc {:number (inc number) :repo-path (repo-path)})]
                                           (when (jals/exists? prev)
                                             [:a {:href (jals/href prev)} (:slug prev)]))]))]
        ]
       doc-html])}))

(defn random-doc [_req]
  (let [target (or
                 (when-let [docs (into [] cat [(olorm/docs {:repo-path (repo-path)})
                                               (jals/docs {:repo-path (repo-path)})])]
                   (let [picked (rand-nth docs)]
                     (cond
                       (= :olorm (:cohort picked)) (olorm/href picked)
                       (= :jals (:cohort picked)) (jals/href picked)
                       :else nil)))
                 "/")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target}
     :body ""}))

(comment
  (olorm/random {:repo-path (repo-path)})
  (jals/random {:repo-path (repo-path)})

  )

(defn hops-info [_req]
  (let [info {:git/sha (System/getenv "HOPS_GIT_SHA")}]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (with-out-str (clojure.pprint/pprint info))}))

(defn css-response [file]
  {:status 200
   :headers {"Content-Type" "text/css"}
   :body (io/file file)})

(defn theme [req]
  (css-response (str "theme/"
                     (get-in req [:route-params :theme]))))
(defn olorm-draw
  "Should draw from http header pool"
  [req]
  (let [pool (get-in req [:params :pool])
        first-letter-names {\o "Oddmund" \l "Lars" \r "Richard"}
        chosen (rand-nth pool)]
    {:status 200
     :headers {"Content-Type" "text/html"
               "Cache-Control" "no-cache"}
     :body (page/html5
            [:head
             [:meta {:charset "utf-8"}]]
            [:body 
             [:h1 "Den heldige som skal skrive olorm er 🥁 "
              (get first-letter-names chosen)]])}))

(defroutes app
  (GET "/" req (index req))
  (GET "/health" _req {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})
  (GET "/vanilla.css" _req (css-response "vanilla.css"))
  (GET "/mikrobloggeriet.css" _req (css-response "mikrobloggeriet.css"))
  (GET "/theme/:theme" req (theme req))
  (GET "/o/" req (olorm-index req))
  (GET "/j/" req (jals-index req))
  (GET "/o/:slug/" req (olorm req))
  (GET "/j/:slug/" req (jals req))
  (GET "/random-doc" _req random-doc) 
  (GET "/hops-info" req (hops-info req))
  (GET "/set-theme/:theme" req (set-theme req))
  (GET "/olorm/draw/:pool" req (olorm-draw req))
  )

(comment
  (app {:uri "/hops-info", :request-method :get})
  (app {:uri "/olorm/draw/o", :request-method :get})

  )

(defonce server (atom nil))
(def port 7223)
(defn stop-server [stop-fn] (when stop-fn (stop-fn)) nil)
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop! [] (swap! server stop-server))
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start! [_opts]
  (swap! server (fn [old-server]
                  (stop-server old-server)
                  (println (str "mikroboggeriet.serve running: http://localhost:" port))
                  (httpkit/run-server #'app {:port port}))))
