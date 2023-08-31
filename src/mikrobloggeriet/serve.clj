(ns mikrobloggeriet.serve
  (:require [babashka.fs :as fs]

            [clojure.java.io :as io] 
            [clojure.edn :as edn]
            [clojure.pprint]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [compojure.core :refer [defroutes GET]]
            [hiccup.page :as page]
            [mikrobloggeriet.cache :as cache] 
            [mikrobloggeriet.store :as store]
            [clj-rss.core :as rss]
            [mikrobloggeriet.cohort :as cohort]
            [mikrobloggeriet.doc :as doc]
            [mikrobloggeriet.jals :as jals]
            [mikrobloggeriet.olorm :as olorm]
            [mikrobloggeriet.pandoc :as pandoc]
            [mikrobloggeriet.style :as style]
            [org.httpkit.server :as httpkit]
            [ring.middleware.cookies :as cookies]
            ))

(defn shared-html-header
  "Shared HTML, including CSS.
  Handles CSS theming system with cookies."
  [req]
  [[:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
   (hiccup.page/include-css "/vanilla.css")
   (hiccup.page/include-css "/mikrobloggeriet.css")
   (let [theme (get-in (cookies/cookies-request req)
                       [:cookies "theme" :value]
                       "vanilla")]
     (hiccup.page/include-css (str "/theme/" theme ".css")))
   (let [theme (get-in (cookies/cookies-request req) [:cookies "theme" :value])
         number (rand-nth (range 4))]
     (when (= theme "iterate")
       [:style {:type "text/css"}
        (str ":root{ --text-color: var(--iterate-base0" number ")}")]))])

(defn feeling-lucky []
  [:a {:href "/random-doc" :class :feeling-lucky} "🎲"])

(defn repo-path [] ".")

(defn set-theme [req]
  (let [target "/"
        theme (or (:theme (:route-params req)) "")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target
               "Set-Cookie" (str "theme=" theme "; Path=/")}
     :body ""}))

(defn- set-flag [req]
  {:status 307
   :headers {"Location" "/"
             "Set-Cookie" (str "flag=" (or (:flag (:route-params req)) "")
                               "; Path=/")}})

(defn- flag [req]
  (get-in (cookies/cookies-request req) [:cookies "flag" :value]))

(def markdown->html+info
  (cache/cache-fn-by (fn markdown->html+info [markdown]
                       (let [pandoc (pandoc/from-markdown markdown)]
                         {:doc-html (pandoc/to-html pandoc)
                          :title (pandoc/infer-title pandoc)}))
                     identity))

(defn read-created-date [file-path]
  (let [content (slurp file-path)
        data (edn/read-string content)]
    (:doc/created data)))

(defn ->java-time-instant [date]
  (.toInstant
   (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") (str date))))

(defn docs->rss-map [docs cohort]
  (let [slugs (map :doc/slug docs)]
    (map (fn [x]  {:title x 
                   :link (str "https://mikrobloggeriet.no" (store/doc-href cohort (doc/from-slug x))) 
                   :pubDate (->java-time-instant (read-created-date (store/doc-meta-path cohort (doc/from-slug x))))  
                   :category (str cohort)}) 
         slugs)))
(defn rss-feed []
  (let [title {:title "Mikrobloggeriet" :link "https://mikrobloggeriet.no"  :description "Mikrobloggeriet: der smått blir stort og hverdagsbetraktninger får mikroskopisk oppmerksomhet"}]
    {:status 200
     :headers {"Content-type" "application/rss+xml"}
     :body (rss/channel-xml title
                            (docs->rss-map (store/docs store/olorm) store/olorm)
                            (docs->rss-map (store/docs store/jals) store/jals)
                            (docs->rss-map (store/docs store/oj) store/oj)
                            (docs->rss-map (store/docs store/genai) store/genai))
     } 
    ) 
  )

(defn index [req]
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
       [:section 
        [:h2 "OLORM"]
        [:p "Mikrobloggen OLORM skrives av Oddmund, Lars og Richard."]
        [:p
         (interpose " · "
                    (for [olorm (->> (olorm/docs {:repo-path (repo-path)})
                                     (map olorm/load-meta)
                                     (pmap (fn [olorm]
                                             (assoc olorm :olorm/title (:title (markdown->html+info (slurp (olorm/index-md-path olorm))))))))]
                      [:a {:href (olorm/href olorm)
                           :title (:olorm/title olorm)}
                       (:slug olorm)]))]]
       [:section
        [:h2 "JALS"]
        [:p "Mikrobloggen JALS skrives av Adrian, Lars og Sindre. Jørgen har skrevet tidligere."]
        [:p
         (interpose " · "
                    (for [doc (jals/docs {:repo-path (repo-path)})]
                      [:a {:href (jals/href doc)} (:slug doc)]))]]

       (when (= "oj" (flag req))
         [:section
          [:h2 "OJ"]
          [:p "Mikrobloggen OJ skrives av Olav og Johan."]
          (interpose " · "
                     (for [doc (cohort/docs cohort/oj)]
                       [:a {:href (store/doc-href store/oj doc  )} (:doc/slug doc)]))])

       (when (= "genai" (flag req))
         [:section
          [:h2 "GENAI"]
          [:p "Mikrobloggen GENAI skrives av ... deg?"]
          (interpose " · "
                     (for [doc (cohort/docs cohort/genai)]
                       [:a {:href (store/doc-href store/genai doc)} (:doc/slug doc)]))])

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
         " Så kan dere bestemme dere for om dere vil fortsette å skrive eller ikke."]]
       [:hr]
       (let [themes (->> (fs/list-dir "theme")
                         (map fs/file-name)
                         (map #(str/replace % #".css$" ""))
                         sort)]
         [:section
          [:p "Sett tema: "
           (into [:span]
                 (interpose " | "
                            (for [t themes]
                              [:a {:href (str "/set-theme/" t)} t])))]
          (let [flag-element (fn [flag-name]
                               [:span
                                (when (= flag-name (flag req))
                                  "🚩 ")
                                [:a {:href (str "/set-flag/" flag-name)} flag-name]])]
            [:p "Sett flagg: "
             (flag-element "ingen-flagg")
             " | "
             (flag-element "oj")
             " | "
             (flag-element "genai")
             ])])])}))

(defn cohort-index [req]
  (page/html5
   (into [:head] (shared-html-header req))
   [:body
    :p
    (feeling-lucky)]))



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
     [:thead
      [:td "olorm"]
      [:td "tittel"]
      [:td "forfatter"]
      [:td "publisert"]]
     [:tbody
      (for [olorm (->> (olorm/docs {:repo-path (repo-path)})
                       (map olorm/load-meta)
                       (pmap (fn [olorm]
                               (assoc olorm :olorm/title (:title (markdown->html+info (slurp (olorm/index-md-path olorm))))))))]
        [:tr
         [:td [:a {:href (olorm/href olorm)} (:slug olorm)]]
         [:td (:olorm/title olorm)]
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

(defn olorm [req]
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
                                             [:a {:href (jals/href prev)} (:slug prev)]))]))]]
       doc-html])}))
(comment
  (cohort/slug store/oj)

  (store/doc-exists? store/oj ( doc/from-slug "oj-2"))
  (let [cohort store/oj
        doc (doc/from-slug "oj-2")
        prev (dec ( doc/number doc))] 
    (store/doc-exists? cohort (doc/from-slug (str (cohort/slug cohort) "-" prev)))
    )
  (store/cohort-href store/oj)
  )
(defn doc
  [req]
  (tap> req)
  (pprint/pprint req)
  (when (and (:mikrobloggeriet/cohort req)
             (:mikrobloggeriet.doc/slug req))
    (let [cohort (:mikrobloggeriet/cohort req)
          doc {:doc/slug (:mikrobloggeriet.doc/slug req)}
          {:keys [title doc-html]}
          (when (doc/exists? cohort doc)
            (markdown->html+info (slurp (doc/index-md-path cohort doc)))) 
          ]
      {:status 200
       :body
       (page/html5
        (into [:head] (concat (when title [[:title title]])
                              (shared-html-header req)))
        [:body
         [:p (feeling-lucky)
          " — "
          [:a {:href "/"} "mikrobloggeriet"]
          " "
          [:a {:href (str "/" (cohort/slug cohort) "/")}
           (cohort/slug cohort)]
          " — " 
          [:span (let [
                       previouse-number (dec (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span [:a {:href (str (store/doc-href cohort prev))} (doc/slug prev)] " · "]))]
          [:span (:doc/slug doc) ]
          [:span (let [previouse-number (inc (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev) 
                     [:span " · " [:a {:href (str (store/doc-href cohort prev))}  (doc/slug prev)]]
                     ))]] 
         doc-html])})))

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
  (jals/random {:repo-path (repo-path)}))

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

(defn draw [req cohort first-letter-names]
  (let [pool (get-in req [:params :pool])
        chosen (rand-nth pool)
        beige "#FAAB66"
        neon-green "#15ff4f"
        ]
    {:status 200
     :header {"Content-Type" "text/html"
              "Cache-Control" "no-cache"}
     :body (page/html5
            [:head
             [:meta {:charset "utf-8"}]
             [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
             (hiccup.page/include-css "/reset.css")]
            [:body {:style (style/inline {:background beige})}
             [:div {:style (style/inline {:margin-left "2rem"
                                          :margin-right "2rem"
                                          :height "100vh"
                                          :display "flex"
                                          :align-items "center"})}
              [:div {:style (style/inline {:font-family "monospace"
                                           :font-size "1.8rem"
                                           :padding "0.8rem"
                                           :line-height "1.4"
                                           :width "100%"
                                           :border-radius "10px"
                                           :background "black"
                                           :color neon-green})}
               [:div (str "$ " (name cohort) " draw " pool)]
               [:div (str (get first-letter-names chosen) " 🎉")]]]])}))

(defroutes app
  (GET "/" req (index req))
  (GET "/health" _req {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})
  (GET "/vanilla.css" _req (css-response "vanilla.css"))
  (GET "/mikrobloggeriet.css" _req (css-response "mikrobloggeriet.css"))
  (GET "/reset.css" _req (css-response "reset.css"))
  (GET "/theme/:theme" req (theme req))
  (GET "/o/" req (olorm-index req))
  (GET "/j/" req (jals-index req))
  (GET "/o/:slug/" req (olorm req))
  (GET "/olorm/:slug/" req (olorm req))
  (GET "/j/:slug/" req (jals req))
  (GET "/jals/:slug/" req (jals req))
  (GET "/random-doc" _req random-doc)
  (GET "/hops-info" req (hops-info req))
  (GET "/set-theme/:theme" req (set-theme req))
  (GET "/set-flag/:flag" req (set-flag req))
  (GET "/olorm/draw/:pool" req (draw req :olorm
                                     {\o "oddmund" \l "lars" \r "richard"}))
  (GET "/jals/draw/:pool" req (draw req :jals
                                    {\a "adrian" \l "lars" \s "sindre"}))
  (GET "/oj/:slug/" req (doc (assoc req
                                   :mikrobloggeriet/cohort cohort/oj
                                   :mikrobloggeriet.doc/slug (get-in req [:route-params :slug]))))
  (GET "/genai/:slug/" req (doc (assoc req
                                      :mikrobloggeriet/cohort cohort/genai
                                      :mikrobloggeriet.doc/slug (get-in req [:route-params :slug]))))
  (GET "/feed/" req (rss-feed))
  )

(comment
  (app {:uri "/hops-info", :request-method :get})
  (app {:uri "/olorm/draw/o", :request-method :get}))

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

