(ns mikrobloggeriet.serve
  (:require
   [babashka.fs :as fs]
   [clj-rss.core :as rss]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint]
   [clojure.string :as str]
   [compojure.core :refer [defroutes GET]]
   [hiccup.page :as page]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.doc-meta :as doc-meta]
   [mikrobloggeriet.http :as http]
   [mikrobloggeriet.pandoc :as pandoc]
   [mikrobloggeriet.store :as store]
   [mikrobloggeriet.urlog :as urlog]
   [mikrobloggeriet.urlog2 :as urlog2]
   [mikrobloggeriet.urlog3 :as urlog3]
   [org.httpkit.server :as httpkit]
   [ring.middleware.cookies :as cookies]))

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
                       "christmas")]
     (hiccup.page/include-css (str "/theme/" theme ".css")))
   (let [theme (get-in (cookies/cookies-request req) [:cookies "theme" :value])
         number (rand-nth (range 4))]
     (when (= theme "iterate")
       [:style {:type "text/css"}
        (str ":root{ --text-color: var(--iterate-base0" number ")}")]))])

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

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
    (map (fn [slug]
           (let [doc (doc/from-slug slug)]
             {:title slug
              :link (str "https://mikrobloggeriet.no" (store/doc-href cohort doc))
              :pubDate (->java-time-instant (read-created-date (store/doc-meta-path cohort doc)))
              :category (cohort/slug cohort)
              :description slug
              :guid slug
              "content:encoded" (str
                                 "<![CDATA["
                                 (:doc-html (markdown->html+info (slurp (store/doc-md-path cohort doc))))
                                 "]]>")}))
         slugs)))

(defn rss-feed []
  (let [title {:title "Mikrobloggeriet" :link "https://mikrobloggeriet.no" :feed-url "https://mikrobloggeriet.no/feed/" :description "Mikrobloggeriet: der smått blir stort og hverdagsbetraktninger får mikroskopisk oppmerksomhet"}]
    {:status 200
     :headers {"Content-type" "application/rss+xml"}
     :body (rss/channel-xml title
                            (docs->rss-map (store/docs store/olorm) store/olorm)
                            (docs->rss-map (store/docs store/jals) store/jals)
                            (docs->rss-map (store/docs store/oj) store/oj)
                            (docs->rss-map (store/docs store/genai) store/genai))}))

(defn cohort-doc-table [req cohort]
  (page/html5
   (into [:head] (shared-html-header req))
   [:body
    [:p
     (feeling-lucky "🎄")
     " — "
     [:a {:href "/"} "mikrobloggeriet"]]
    [:h1 (str "Alle " (str/upper-case (cohort/slug cohort)) "-er")]
    [:table
     [:thead
      [:td (cohort/slug cohort)]
      [:td "tittel"]
      [:td "forfatter"]
      [:td "publisert"]]
     [:tbody
      (for [doc (->> (store/docs cohort)
                     (map (fn [doc]
                            (store/load-meta cohort doc)))
                     (pmap (fn [doc]
                             (assoc doc :doc/title (:title (markdown->html+info (slurp (store/doc-md-path cohort doc))))))))]
        [:tr
         [:td [:a {:href (store/doc-href cohort doc)} (doc/slug doc)]]
         [:td (:doc/title doc)]
         [:td (store/author-first-name cohort doc)]
         [:td (:doc/created doc)]])]]]))

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
       [:p (feeling-lucky "🎄")]
       [:h1 "Mikrobloggeriet"]
       [:p "Folk fra Iterate deler fra hverdagen."]
       [:section
        [:h2 "Mikrobloggeriets Julekalender 2023"]
        [:p "Mikrobloggen LUKE skrives av Iterate-ansatte gjennom adventstida 2023."]
        [:ul {:class "doc-list"}
         (let [cohort store/luke]
           (for [doc (->> (store/docs cohort)
                          (map (fn [doc] (store/load-meta cohort doc)))
                          (remove doc-meta/draft?))]
             [:li [:a {:href (store/doc-href cohort doc)} (:doc/slug doc)]]))]]
       [:section
        [:h2 "OLORM"]
        [:p "Mikrobloggen OLORM skrives av Oddmund, Lars, Richard og Teodor."]
        [:ul {:class "doc-list"}
         (let [cohort store/olorm]
           (for [doc (->> (store/docs cohort)
                          (map (fn [doc] (store/load-meta cohort doc)))
                          (remove doc-meta/draft?))]
             [:li [:a {:href (store/doc-href cohort doc)} (:doc/slug doc)]]))]]
       [:section
        [:h2 "JALS"]
        [:p "Mikrobloggen JALS skrives av Adrian, Lars og Sindre. Jørgen har skrevet tidligere."]
        [:ul {:class "doc-list"}
         (let [cohort store/jals]

           (for [doc (->> (store/docs cohort)
                          (map (fn [doc] (store/load-meta cohort doc)))
                          (remove doc-meta/draft?))]
             [:li [:a {:href (store/doc-href cohort doc)} (:doc/slug doc)]]))]]

       [:section
        [:h2 "OJ"]
        [:p "Mikrobloggen OJ skrives av Olav og Johan."]
        [:ul {:class "doc-list"}
         (let [cohort store/oj]

           (for [doc (->> (store/docs cohort)
                          (map (fn [doc] (store/load-meta cohort doc)))
                          (remove doc-meta/draft?))]
             [:li [:a {:href (store/doc-href cohort doc)} (:doc/slug doc)]]))]]

       (when (= "genai" (flag req))
         [:section
          [:h2 "GENAI"]
          [:p "Mikrobloggen GENAI skrives av ... deg?"]
          (let [cohort store/genai]
            (interpose " · "
                       (for [doc (->> (store/docs cohort)
                                      (map (fn [doc] (store/load-meta cohort doc)))
                                      (remove doc-meta/draft?))]
                         [:a {:href (store/doc-href cohort doc)} (:doc/slug doc)])))])

       (urlog/index-section req)

       [:hr]

       [:section
        [:h2 "Hva er dette for noe?"]
        [:p
         "Mikrobloggeriet er et initiativ der folk fra " [:a {:href iterate-url} "Iterate"] " deler ting de bryr seg om i hverdagen. "
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
        [:h2 "Jeg jobber i Iterate og vil skrive, hva gjør jeg?"]
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
             (flag-element "genai")
             " | "
             (flag-element "god-jul")])])])}))

(comment
  (cohort/slug store/oj)

  (store/doc-exists? store/oj (doc/from-slug "oj-2"))

  (let [cohort store/oj
        doc (doc/from-slug "oj-2")
        prev (dec (doc/number doc))]
    (store/doc-exists? cohort (doc/from-slug (str (cohort/slug cohort) "-" prev))))

  (store/cohort-href store/oj))

(defn doc
  [req cohort]
  (when (:slug (:route-params req))
    (let [doc (doc/from-slug (:slug (:route-params req)))
          {:keys [title doc-html]}
          (when (store/doc-exists? cohort doc)
            (markdown->html+info (slurp (store/doc-md-path cohort doc))))]
      {:status 200
       :body
       (page/html5
        (into [:head] (concat (when title [[:title title]])
                              (shared-html-header req)))
        [:body
         [:p (feeling-lucky "🎄")
          " — "
          [:a {:href "/"} "mikrobloggeriet"]
          " "
          [:a {:href (str "/" (cohort/slug cohort) "/")}
           (cohort/slug cohort)]
          " — "
          [:span (let [previouse-number (dec (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span [:a {:href (str (store/doc-href cohort prev))} (doc/slug prev)] " · "]))]
          [:span (:doc/slug doc)]
          [:span (let [previouse-number (inc (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span " · " [:a {:href (str (store/doc-href cohort prev))}  (doc/slug prev)]]))]]
         doc-html])})))

(defn random-doc [_req]
  (let [target (or
                (when-let [[cohort doc] (store/random-cohort+doc)]
                  (store/doc-href cohort doc))
                "/")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target}
     :body ""}))

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

(defroutes app
  (GET "/" req (index req))

  ;; STATIC FILES
  (GET "/health" _req {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})
  (GET "/vanilla.css" _req (css-response "vanilla.css"))
  (GET "/mikrobloggeriet.css" _req (css-response "mikrobloggeriet.css"))
  (GET "/reset.css" _req (css-response "reset.css"))
  (GET "/urlog.css" _req (css-response "urlog.css")) ;; NENO STUFF

  ;; THEMES AND FEATURE FLAGGING
  (GET "/set-theme/:theme" req (set-theme req))
  (GET "/set-flag/:flag" req (set-flag req))
  (GET "/theme/:theme" req (theme req))

  ;; STUFF
  (GET "/feed/" _req (rss-feed))
  (GET "/hops-info" req (hops-info req))
  (GET "/random-doc" _req random-doc)

  ;; OLORM
  ;; /o/* URLS are deprecated in favor of /olorm/* URLs
  (GET "/o/" _req (http/permanent-redirect {:target "/olorm/"}))
  (GET "/olorm/" req (cohort-doc-table req store/olorm))

  (GET "/o/:slug/" req (http/permanent-redirect {:target (str "/olorm/" (:slug (:route-params req)) "/")}))
  (GET "/olorm/:slug/" req (doc req store/olorm))

  ;; JALS
  ;; /j/* URLS are deprecated in favor of /jals/* URLs
  (GET "/j/" _req (http/permanent-redirect {:target "/jals/"}))
  (GET "/jals/" req (cohort-doc-table req store/jals))
  (GET "/j/:slug/" req (http/permanent-redirect {:target (str "/jals/" (:slug (:route-params req)) "/")}))
  (GET "/jals/:slug/" req (doc req store/jals))

  ;; OJ
  (GET "/oj/" req (cohort-doc-table req store/oj))
  (GET "/oj/:slug/" req (doc req store/oj))

  ;; GENAI
  (GET "/genai/" req (cohort-doc-table req store/genai))
  (GET "/genai/:slug/" req (doc req store/genai))

  ;; LUKE 
  (GET "/luke/" req (cohort-doc-table req store/luke))
  (GET "/luke/:slug/" req (doc req store/luke))

  ;; NENO
  (GET "/urlog/" req (urlog/urlogs req))
  (GET "/urlog/:slug/" req (urlog/doc req store/urlog))

  ;; NENO 2
  (GET "/urlog2/" req (urlog2/urlogs req))
  (GET "/urlog3/" req (urlog3/urlogs req)))

(comment
  (app {:uri "/olorm/olorm-1/", :request-method :get})
  (app {:uri "/hops-info", :request-method :get}))

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

