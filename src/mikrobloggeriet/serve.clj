(ns mikrobloggeriet.serve
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.pprint]
   [clojure.string :as str]
   [datomic.api :as d]
   [hiccup.page :as page]
   [mikrobloggeriet.asset :as asset]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.cohort.urlog :as cohort.urlog]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.http :as http]
   [mikrobloggeriet.ui.cohort :as ui.cohort]
   [mikrobloggeriet.ui.doc :as ui.doc]
   [mikrobloggeriet.ui.index :as ui.index]
   [mikrobloggeriet.ui.shared :as ui.shared]
   [reitit.core :as reitit]
   [reitit.ring]
   [ring.middleware.cookies :as cookies]))

(declare create-ring-handler)
(declare url-for)

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

(comment
  (def db mikrobloggeriet.state/datomic)
  (def olorm (d/entity db [:cohort/id :cohort/olorm]))
  )

(defn index [req]
  (let [mikrobloggeriet-announce-url "https://garasjen.slack.com/archives/C05355N5TCL"
        github-mikrobloggeriet-url "https://github.com/iterate/mikrobloggeriet/"
        _tech-forum-url "https://garasjen.slack.com/archives/C2K35RDNJ"
        hops-url "https://www.headless-operations.no/"
        iterate-url "https://www.iterate.no/"
        datomic (:mikrobloggeriet.system/datomic req)]
    {:status 200
     :headers {"Content-type" "text/html"}
     :body
     (page/html5
         (into [:head] (ui.shared/html-header req))
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

(defn random-doc [req]
  (let [db (:mikrobloggeriet.system/datomic req)
        target (or
                (when-let [doc (doc/random-doc db)]
                  (doc/href doc))
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

(defn deploy-info [_req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body
   (with-out-str
     (clojure.pprint/pprint
      {:last-modified-file-time
       (str (fs/last-modified-time
             (last-modified-file "." "**/*.{js,css,html,clj,md,edn}")))}))})

(defn css-response [file]
  {:status 200
   :headers {"Content-Type" "text/css"}
   :body (io/file file)})

(defn theme [req]
  (css-response (str "theme/" (http/path-param req :theme))))

(defn health [_req]
  {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})

(defn markdown-cohort-routes [cohort-data]
  [(str "/" (:cohort/slug cohort-data))
   ["/" {:get (fn [req]
                (let [db (:mikrobloggeriet.system/datomic req)
                      cohort (d/entity db [:cohort/slug (:cohort/slug cohort-data)])]
                  (ui.cohort/doc-table db cohort req)))
         :name (keyword (str "mikrobloggeriet." (:cohort/slug cohort-data))
                        "all")}]
   ["/:slug/" {:get (fn [req]
                      (let [db (:mikrobloggeriet.system/datomic req)
                            doc-slug (http/path-param req :slug)
                            doc (d/entity db [:doc/slug doc-slug])]
                        (ui.doc/page doc
                                     req
                                     (merge
                                      (when-let [previous (doc/previous db doc)]
                                        {:previous previous})
                                      (when-let [next (doc/next db doc)]
                                        {:next next})))))}]])

(comment
  (markdown-cohort-routes (:cohort/olorm db/cohorts))

  )

(defn create-ring-handler
  []
  (reitit.ring/ring-handler
   (reitit.ring/router
    (concat

     ;; CSS files we use
     (for [css-file ["vanilla.css" "mikrobloggeriet.css" "pygment.css" "reset.css" "urlog.css"]]
       [(str "/" css-file) {:get (fn [_req]
                                   (css-response css-file))
                            :name (keyword "mikrobloggeriet.default-css"
                                           css-file)}])
     [ ;; Front page
      ["/" {:get #'index ; forside
            :head #'health ; helsesjekk, Application.garden
            :name :mikrobloggeriet/frontpage}]

      ;; Themes
      ["/theme/:theme" {:get #'theme
                        :name :mikrobloggeriet/theme}]
      ["/set-theme/:theme" {:get #'set-theme
                            :name :mikrobloggeriet/set-theme}]
      ;; Feature flags
      ["/set-flag/:theme" {:get #'set-flag
                           :name :mikrobloggeriet/set-flag}]]

     ;; Markdown cohorts
     (for [c (->> (vals db/cohorts)
                  (filter #(= :cohort.type/markdown (:cohort/type %))))]
       (markdown-cohort-routes c))

     ;; Urlog
     [["/urlog/" {:get #'cohort.urlog/page
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
      ["/random-doc" {:get #'random-doc
                      :name :mikrobloggeriet/random-doc}]

      ;; Deploy
      ["/deploy-info" {:get #'deploy-info :name :mikrobloggeriet/deploy-info}]

      ;; helsesjekk, HOPS
      ["/health" {:get health
                  :name :mikrobloggeriet/health}]

      ["/last-modified-file-time" {:name :mikrobloggeriet/last-modified-file-time
                                   :get #'last-modified-file-handler}]

      ["/images/:image-path" {:get (fn [req]
                                     (let [image-path (http/path-param req :image-path)]
                                       (asset/load-image image-path)))}]
      ]))
   (reitit.ring/redirect-trailing-slash-handler)))

(defn url-for
  ([name] (url-for name {}))
  ([name path-params]
   nil
   #_
   (->
    (reitit.ring/get-router app)
    (reitit/match-by-name name)
    (reitit/match->path path-params))))
