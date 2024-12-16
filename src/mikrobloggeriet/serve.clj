(ns mikrobloggeriet.serve
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.pprint]
   [clojure.string :as str]
   [datomic.api :as d]
   [hiccup.page :as page]
   [mikrobloggeriet.asset :as asset]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.cohort.urlog :as cohort.urlog]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.http :as http]
   [mikrobloggeriet.pandoc :as pandoc]
   [mikrobloggeriet.store :as store]
   [mikrobloggeriet.ui.index :as ui.index]
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
   (hiccup.page/include-css "/pygment.css")
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

(comment
  (cohort/slug store/olorm))

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
                               (assoc doc :doc/title (:title (cache/markdown->html+info (slurp (store/doc-md-path cohort doc))))))))]
          [:tr
           [:td [:a {:href (store/doc-href cohort doc)} (doc/slug doc)]]
           [:td (:doc/title doc)]
           [:td (store/author-first-name cohort doc)]
           [:td (:doc/created doc)]])]]])})

(comment
  (def db (:mikrobloggeriet.system/datomic @mikrobloggeriet.repl/state))
  (into {} (d/entity db [:cohort/id :cohort/olorm]))
  )

(defn index [req]
  (let [mikrobloggeriet-announce-url "https://garasjen.slack.com/archives/C05355N5TCL"
        github-mikrobloggeriet-url "https://github.com/iterate/mikrobloggeriet/"
        _tech-forum-url "https://garasjen.slack.com/archives/C2K35RDNJ"
        teodor-url "https://teod.eu/"
        hops-url "https://www.headless-operations.no/"
        iterate-url "https://www.iterate.no/"
        datomic (:mikrobloggeriet.system/datomic req)]
    {:status 200
     :headers {"Content-type" "text/html"}
     :body
     (page/html5
         (into [:head] (shared-html-header req))
         [:body
          [:p (feeling-lucky "🎲")]
          [:h1 "Mikrobloggeriet"]
          [:p "Folk fra Iterate deler fra hverdagen!"]

          (ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/olorm]))
(ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/jals]))
(ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/iterate]))

          (let [urlog (d/entity datomic [:cohort/id :cohort/urlog])]
            [:section
             [:h2 (:cohort/name urlog)]
             [:p (:cohort/description urlog)]
             [:p [:a {:href (cohort/href urlog)}
                  "Gå inn i huset –> 🏨"]]])

          (ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/oj]))
(ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/luke]))
(ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/vakt]))
(ui.index/cohort-section (d/entity datomic [:cohort/id :cohort/kiel]))

          [:hr]

          [:section
           [:h2 "Hva er dette for noe?"]
           [:p
            "Mikrobloggeriet er et initiativ der folk fra " [:a {:href iterate-url} "Iterate"] " deler ting de bryr seg om i hverdagen. "
            "Vi publiserer fritt tilgjengelig på Internett fordi vi har tro på å dele kunnskap. "
            "Innhold og kode for Mikrobloggeriet på " [:a {:href github-mikrobloggeriet-url} "github.com/iterate/mikrobloggeriet"] ". "
            "Mikrobloggeriet kjører på " [:a {:href hops-url} "Headless Operations"] ". "]]

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
  (when-let [slug (http/path-param req :slug)]
    (let [doc (doc/from-slug slug)
          {:keys [title doc-html]}
          (when (store/doc-exists? cohort doc)
            (cache/markdown->html+info (slurp (store/doc-md-path cohort doc))))]
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

(defn last-modified-file [root match]
  (apply max-key
         (comp fs/file-time->millis fs/last-modified-time)
         (fs/glob root match)))

(defn last-modified-file-handler
  "Endpoint that can be used from HTTP clients to provide live reloading

  For example as an opt-in experience when working on HTML / Clojure"
  [_req]
  (let [last-modified (last-modified-file "." "**/*.{clj,css,edn,html,js,md}")]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (str (fs/last-modified-time last-modified) "\n")}))

(defn deploy-info [req]
  (let [env (System/getenv)
        last-modified (last-modified-file "." "**/*.{js,css,html,clj,md,edn}")
        info {:git/sha (get env  "HOPS_GIT_SHA")
              :last-modified-file-time (str (fs/last-modified-time last-modified))
              ;; :env-keys (keys env)
              }]
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

     ;; CSS files we use
     (for [css-file ["vanilla.css" "mikrobloggeriet.css" "pygment.css" "reset.css" "urlog.css"]]
       [(str "/" css-file) {:get (constantly (css-response css-file))
                            :name (keyword "mikrobloggeriet.default-css"
                                           css-file)}])
     [ ;; Front page
      ["/" {:get index ; forside
            :head health ; helsesjekk, Application.garden
            :name :mikrobloggeriet/frontpage}]

      ;; Themes
      ["/theme/:theme" {:get theme
                        :name :mikrobloggeriet/theme}]
      ["/set-theme/:theme" {:get set-theme
                            :name :mikrobloggeriet/set-theme}]
      ;; Feature flags
      ["/set-flag/:theme" {:get set-flag
                           :name :mikrobloggeriet/set-flag}]]

     ;; Markdown cohorts
     (for [c [store/olorm store/jals store/oj store/luke store/vakt store/kiel store/cohort-iterate]]
       (markdown-cohort-routes c))

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

     ;; DIV
     [ ;; Go to a random document
      ["/random-doc" {:get random-doc
                      :name :mikrobloggeriet/random-doc}]

      ;; Deploy
      ["/deploy-info" {:get deploy-info
                       :name :mikrobloggeriet/deploy-info}]

      ;; helsesjekk, HOPS
      ["/health" {:get health
                  :name :mikrobloggeriet/health}]

      ["/last-modified-file-time" {:name :mikrobloggeriet/last-modified-file-time
                                   :get last-modified-file-handler}]

      ["/images/:image-path" {:get (fn [req]
                                     (let [image-path (http/path-param req :image-path)]
                                       (asset/load-image image-path)))}]
      ]))
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

(comment
  ((app) {:uri "/deploy-info", :request-method :get})
  :rcf)

(comment
  (def app-instance (app))
  (app-instance {:uri "/olorm/olorm-1/" :request-method :get})
  :rfc)
