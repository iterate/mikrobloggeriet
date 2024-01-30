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
   [mikrobloggeriet.cohort.urlog :as cohort.urlog]
   [org.httpkit.server :as httpkit]
   [reitit.ring]
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
                       "vanilla")]
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
        theme (or (http/path-param req :theme) "")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target
               "Set-Cookie" (str "theme=" theme "; Path=/")}
     :body ""}))

(defn- set-flag [req]
  {:status 307
   :headers {"Location" "/"
             "Set-Cookie" (str "flag=" (or (http/path-param req :flag) "")
                               "; Path=/")}})

(defn- flag [req]
  (get-in (cookies/cookies-request req) [:cookies "flag" :value]))

(def markdown->html+info
  (cache/cache-fn-by (fn markdown->html+info [markdown]
                       (let [pandoc (pandoc/from-markdown markdown)]
                         {:doc-html (pandoc/to-html pandoc)
                          :title (pandoc/infer-title pandoc)}))
                     identity))

(defn cohort-rss-section [cohort]
  (for [doc (store/docs cohort)]
    {:title (doc/slug doc)
     :link (str "https://mikrobloggeriet.no" (store/doc-href cohort doc))
     :pubDate (doc-meta/created-instant (store/load-meta cohort doc))
     :category (cohort/slug cohort)
     :description (doc/slug doc)
     :guid (doc/slug doc)
     "content:encoded" (str
                        "<![CDATA["
                        (:doc-html (markdown->html+info (slurp (store/doc-md-path cohort doc))))
                        "]]>")}))

(comment
  (cohort/slug store/olorm))

(defn rss-feed [_req]
  (let [title {:title "Mikrobloggeriet" :link "https://mikrobloggeriet.no" :feed-url "https://mikrobloggeriet.no/feed/" :description "Mikrobloggeriet: der smått blir stort og hverdagsbetraktninger får mikroskopisk oppmerksomhet"}]
    {:status 200
     :headers {"Content-type" "application/rss+xml"}
     :body (rss/channel-xml title
                            (cohort-rss-section store/olorm)
                            (cohort-rss-section store/jals)
                            (cohort-rss-section store/oj))}))

(comment
  (def rss1 (rss-feed {}))
  (spit "rss.xml" (:body rss1))
  )

(defn cohort-doc-table [req cohort]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body
   (page/html5
       (into [:head] (shared-html-header req))
     [:body
      [:p
       (feeling-lucky "🎲")
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
           [:td (:doc/created doc)]])]]])})

(defn default-cohort-section [cohort name description]
  [:section
   [:h2 name]
   [:p description]
   [:ul {:class "doc-list"}
    (for [doc (store/published-docs cohort)]
      [:li [:a {:href (store/doc-href cohort doc)} (:doc/slug doc)]])]])

(defn index [req]
  (let [mikrobloggeriet-announce-url "https://garasjen.slack.com/archives/C05355N5TCL"
        github-mikrobloggeriet-url "https://github.com/iterate/mikrobloggeriet/"
        _tech-forum-url "https://garasjen.slack.com/archives/C2K35RDNJ"
        teodor-url "https://teod.eu/"
        hops-url "https://www.headless-operations.no/"
        iterate-url "https://www.iterate.no/"]
    {:status 200
     :headers {}
     :body
     (page/html5
      (into [:head] (shared-html-header req))
      [:body
       [:p (feeling-lucky "🎲")]
       [:h1 "Mikrobloggeriet"]
       [:p "Folk fra Iterate deler fra hverdagen."]

       (default-cohort-section store/olorm "OLORM" "Mikrobloggen OLORM skrives av Oddmund, Lars, Richard og Teodor.")
       (default-cohort-section store/jals "JALS" "Mikrobloggen JALS skrives av Adrian, Lars og Sindre. Jørgen har skrevet tidligere.")

       [:section
        [:h2 "URLOG"]
        [:p "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]
        [:p [:a {:href "/urlog/"} "Gå inn i huset –> 🏨"]]]
       (default-cohort-section store/oj "OJ" "Mikrobloggen OJ skrives av Olav og Johan.")
       (default-cohort-section store/luke "Mikrobloggeriets Julekalender 2023" "Mikrobloggen LUKE ble skrevet av Iterate-ansatte gjennom adventstida 2023.")
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
  (when (http/path-param req :slug)
    (let [doc (doc/from-slug (http/path-param req :slug))
          {:keys [title doc-html]}
          (when (store/doc-exists? cohort doc)
            (markdown->html+info (slurp (store/doc-md-path cohort doc))))]
      {:status 200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body
       (page/html5
        (into [:head] (concat (when title [[:title title]])
                              (shared-html-header req)))
        [:body
         [:p (feeling-lucky "🎲")
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
  (css-response (str "theme/" (http/path-param req :theme))))

(defn health [_req]
  {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})

(defroutes app
  (GET "/" req (index req))

  ;; STATIC FILES
  (GET "/health" _req health)
  (GET "/vanilla.css" _req (css-response "vanilla.css"))
  (GET "/mikrobloggeriet.css" _req (css-response "mikrobloggeriet.css"))
  (GET "/reset.css" _req (css-response "reset.css"))
  (GET "/urlog.css" _req (css-response "urlog.css")) ;; NENO STUFF
  ;; THEMES AND FEATURE FLAGGING
  (GET "/set-theme/:theme" req (set-theme req))
  (GET "/set-flag/:flag" req (set-flag req))
  (GET "/theme/:theme" req (theme req))

  ;; STUFF
  (GET "/feed/" _req (rss-feed _req))
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

  ;; LUKE 
  (GET "/luke/" req (cohort-doc-table req store/luke))
  (GET "/luke/:slug/" req (doc req store/luke))

  ;; NENO
  (GET "/urlog/" req (cohort.urlog/page req))
  )

(comment
  (app {:uri "/olorm/olorm-1/", :request-method :get})
  (app {:uri "/hops-info", :request-method :get})
  :rcf)

;; ## Ekspriment, bør vi bruke Reitit?
;;
;; Hvorfor?
;;
;; - Les begrunnelse og diskusjon i tråd:
;;   https://garasjen.slack.com/archives/C05MH5RCLH3/p1706353191440179
;;
;; Hvordan?
;;
;; - Vi prøver oss på å implementere Reitit i prod ved siden av Compojure.
;; - Og vi prøver å bruke orakeltesting for å sjekke hvordan dette går.
;;   (forslag fra Oddmund i diskusjonstråden)

(defn reitit-cohort-routes [cohort]
  [(str "/" (cohort/slug cohort))
   ["/" {:get (fn [req] (cohort-doc-table req cohort))
         :name (keyword (str "mikrobloggeriet." (cohort/slug cohort))
                        "all")}]
   ["/:slug/" {:get (fn [req] (doc req cohort))}] ])

(defn ^:experimental
  app-reitit
  []
  (reitit.ring/ring-handler
   (reitit.ring/router
    (concat

     ;; Deployment aides
     [["/hops-info" {:get hops-info
                     :name :mikrobloggeriet/hops-info}]
      ["/health" {:get health
                  :name :mikrobloggeriet/health}]]

     ;; Arbitrary CSS files
     (for [css-file ["vanilla.css" "mikrobloggeriet.css" "reset.css" "urlog.css"]]
       [(str "/" css-file) {:get (constantly (css-response css-file))
                            :name (keyword "mikrobloggeriet.default-css"
                                           css-file)}])
     ;; Theme support
     [["/theme/:theme" {:get theme
                        :name :mikrobloggeriet/theme}]
      ["/" {:get index
            :name :mikrobloggeriet/frontpage}]]

     ;; Markdown cohorts
     (for [c [store/olorm store/jals store/oj store/luke]]
       (reitit-cohort-routes c))

     ;; RSS
     [["/feed/" {:get rss-feed
                 :name :mikrobloggeriet/feed}]]

     ;; Urlog
     [["/urlog/" {:get cohort.urlog/page
                  :name :mikrobloggeriet.urlog/all}]]
     ;; TODO redirects
     [["/o/" {:get (constantly (http/permanent-redirect {:target "/olorm/"}))}]]
     ))))

(defonce
  ^{:doc "Compatibility report for compojure and reitit router.

In dev:

    http://localhost:7223/app12-compat-report

In prod:

    https://mikrobloggeriet.no/app12-compat-report"}
  app12-compat (atom (sorted-map)))

(comment
  (reset! app12-compat (sorted-map))

  (let [uri "/o/"]
    (=
     ((app-reitit) {:request-method :get :uri uri})
     (app {:request-method :get :uri uri})))

  :rcf)

(defn app12-compat-report [req]
  {:status 200
   :headers {}
   :body
   (page/html5 (into [:head] (shared-html-header req))
     [:body
      [:p
       (feeling-lucky "🎲")
       " — "
       [:a {:href "/"} "mikrobloggeriet"]]
      [:h1 "Kompatibilitetsrapport gammel/ny router"]
      [:table
       [:thead
        [:td "HTTP Request"]
        [:td "Lik respons gammel/ny?"]]
       [:tbody
        (for [[[method uri] v] @app12-compat]
          [:tr
           [:td
            [:code (pr-str method)]
            " "
            [:code [:a {:href uri} uri]]]
           [:td [:code (pr-str v)]]])]]])})

(defn app-with-reitit-test
  "Temporary wrapper around `app` while we implement Reitit

  This handler calls Mikrobloggeriet through the old Compojure router, and also
  through the new Reitit router. Compojure / Reitit compatibility status can be
  viewed on /app12-compat-report.

  For details, see https://github.com/iterate/mikrobloggeriet/pull/81"
  [default-app experimental-app req]
  (cond
    ;; Report is special!
    (= (:uri req) "/app12-compat-report")
    (app12-compat-report req)

    ;; Not a GET or HEAD request?
    ;; Don't send two requests.
    (not (#{:get :head} (:request-method req)))
    ((default-app) req)

    :else
    (let [response-1 ((default-app) req)
          response-2 ((experimental-app) req)
          req->key (juxt :request-method :uri)]
      (when (http/response-ok? response-1)
        ;; Compare and report
        (if (= response-1 response-2)
          (swap! app12-compat assoc (req->key req) :ok)
          (swap! app12-compat assoc (req->key req) :not-ok)))
      ;; Return the old value
      response-1)))

;; ## REPL-grensesnitt

(defonce server (atom nil))
(def port 7223)
(defn stop-server [stop-fn] (when stop-fn (stop-fn)) nil)
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop! [] (swap! server stop-server))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start! [_opts]
  (swap! server
         (fn [old-server]
           (stop-server old-server)
           (println (str "mikroboggeriet.serve running: http://localhost:" port))
           (httpkit/run-server #(app-with-reitit-test (constantly app)
                                                      #'app-reitit
                                                      %)
                               {:port port}))))

(comment
  ((app-reitit) {:uri "/hops-info", :request-method :get})

  (let [f #(app-with-reitit-test (constantly app)
                                 #'app-reitit
                                 %)]
    (f {:uri "/hops-info", :request-method :get}))
  :rcf)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start-prod! [_opts]
  (swap! server
         (fn [old-server]
           (stop-server old-server)
           (println (str "mikroboggeriet.serve running: http://localhost:" port))
           (httpkit/run-server #(app-with-reitit-test (constantly app)
                                                      (constantly (app-reitit))
                                                      %)
                               {:port port}))))
