(ns mikrobloggeriet.serve
  (:require
   [babashka.fs :as fs]
   [clj-rss.core :as rss]
   [clojure.java.io :as io]
   [clojure.pprint]
   [clojure.string :as str]
   [hiccup.page :as page]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.cohort.urlog :as cohort.urlog]
   [mikrobloggeriet.config :as config]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.doc-meta :as doc-meta]
   [mikrobloggeriet.http :as http]
   [mikrobloggeriet.pandoc :as pandoc]
   [mikrobloggeriet.store :as store]
   [org.httpkit.server :as httpkit]
   [reitit.core :as reitit]
   [reitit.ring]
   [ring.middleware.cookies :as cookies]))

(declare app)
(declare url-for)

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

(comment

  (url-for :mikrobloggeriet/random-doc)
  )

(defn feeling-lucky [content]
  [:a {:href (url-for :mikrobloggeriet/random-doc {}) :class :feeling-lucky} content])

(comment
  (feeling-lucky "")
  ;; => [:a {:href "/random-doc", :class :feeling-lucky} ""]
  )

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
       (default-cohort-section store/luke
                               "Mikrobloggeriets Julekalender 2023"
                               "Mikrobloggen LUKE ble skrevet av Iterate-ansatte gjennom adventstida 2023.")
       (default-cohort-section store/vakt
                               "VAKT"
                               "Fra oss som lager Mikrobloggeriet.")
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

(defn deploy-info [_req]
  (let [env (System/getenv)
        info {:git/sha (get env  "HOPS_GIT_SHA")
              :env-keys (keys env)
              :db-cofig-keys (keys (db/hops-config env))}]
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

(defn markdown-cohort-routes [cohort]
  [(str "/" (cohort/slug cohort))
   ["/" {:get (fn [req] (cohort-doc-table req cohort))
         :name (keyword (str "mikrobloggeriet." (cohort/slug cohort))
                        "all")}]
   ["/:slug/" {:get (fn [req] (doc req cohort))}] ])

(defn app
  []
  (reitit.ring/ring-handler
   (reitit.ring/router
    (concat

     ;; Deployment aides
     [["/deploy-info" {:get deploy-info
                     :name :mikrobloggeriet/deploy-info}]
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
     (for [c [store/olorm store/jals store/oj store/luke store/vakt]]
       (markdown-cohort-routes c))

     ;; RSS
     [["/feed/" {:get rss-feed
                 :name :mikrobloggeriet/feed}]]

     ;; Urlog
     [["/urlog/" {:get cohort.urlog/page
                  :name :mikrobloggeriet.urlog/all}]]

     ;; Support old URLs
     ;; Originally, OLORM was /o/ and JALS was /j/.
     [["/o/" {:get (constantly (http/permanent-redirect {:target "/olorm/"}))}]
      ["/j/" {:get (constantly (http/permanent-redirect {:target "/jals/"}))}]
      ["/o/:slug/" {:get (fn [req]
                           (when-let [slug (http/path-param req :slug)]
                             (http/permanent-redirect {:target (str "/olorm/" slug "/")})))}]
      ["/j/:slug/" {:get (fn [req]
                           (when-let [slug (http/path-param req :slug)]
                             (http/permanent-redirect {:target (str "/jals/" slug "/")})))}]]

     ;; Go to a random document
     [["/random-doc" {:get random-doc
                      :name :mikrobloggeriet/random-doc}]]

     ;; Try if the DB works
     [["/try-db-stuff" {:get db/try-db-stuff
                        :name :mikrobloggeriet/try-db-stuff}]]
     ))
   (reitit.ring/redirect-trailing-slash-handler)))

(defn url-for
  ([name] (url-for name {}))
  ([name path-params]
   (->
    (reitit.ring/get-router (app))
    (reitit/match-by-name name)
    (reitit/match->path path-params))))

(comment
  (url-for :mikrobloggeriet/frontpage)

  (url-for :mikrobloggeriet/theme {:theme "bwb"})
  )

;; ## REPL-grensesnitt

(defonce server (atom nil))
(defn stop-server [stop-fn] (when stop-fn (stop-fn)) nil)
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop! [] (swap! server stop-server))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start! [_opts]
  (swap! server
         (fn [old-server]
           (stop-server old-server)
           (println (str "mikroboggeriet.serve running: http://localhost:" config/http-server-port))
           (httpkit/run-server (fn [req]
                                 ((app) req))
                               {:port config/http-server-port}))))

(comment
  ((app) {:uri "/deploy-info", :request-method :get})
  :rcf)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start-prod! [_opts]
  (swap! server
         (fn [old-server]
           (stop-server old-server)
           (println (str "mikroboggeriet.serve running: http://localhost:" config/http-server-port))
           (httpkit/run-server (app)
                               {:port config/http-server-port}))))
