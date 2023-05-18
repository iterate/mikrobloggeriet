(ns olorm.serve
  (:require
   [compojure.core :refer [defroutes GET]]
   [hiccup.page :as page]
   [iki.api :as iki]
   [mikrobloggeriet.olorm :as olorm]
   [mikrobloggeriet.jals :as jals]
   [clojure.pprint]
   [org.httpkit.server :as httpkit]))

(defn shared-html-header
  "Shared header content -- for example CSS imports."
  []
  [(hiccup.page/include-css "/vanilla.css")
   (hiccup.page/include-css "/mikrobloggeriet.css")])

(defn roulette []
  [:a {:href "/random-doc"} "rulett"])

(defn index [_req]
  (let [mikrobloggeriet-announce-url "https://garasjen.slack.com/archives/C05355N5TCL"
        github-olorm-url "https://github.com/iterate/olorm/"
        tech-forum-url "https://garasjen.slack.com/archives/C2K35RDNJ"
        teodor-url "https://teod.eu/"
        hops-url "https://www.headless-operations.no/"
        iterate-url "https://www.iterate.no/"]
    (page/html5
     (into [:head]
           (shared-html-header))
     [:body
      [:p
       (roulette)]
      [:h1 "Mikrobloggeriet"]
      [:p "Et initiativ for mikroblogging."]
      [:h2 "OLORM"]
      [:p "OLORM er en mikroblogg skrevet av Oddmund, Lars og Richard."]
      [:p
       (interpose " · "
                  (for [olorm (olorm/docs {:repo-path ".."})]
                    [:a {:href (olorm/href olorm)} (:slug olorm)]))]
      [:h2 "JALS"]
      [:p "JALS er en mikroblogg skrevet av Jørgen, Adrian, Lars og Sindre."]
      [:p
       (interpose " · "
                  (for [doc (jals/docs {:repo-path ".."})]
                    [:a {:href (jals/href doc)} (:slug doc)]))]

      [:hr]
      [:h2 "Hva er dette, " [:em "egentlig"] "?"]
      [:p [:strong "For Iterate-ansatte:"] " "
       "Diskuter gjerne mikroblogginnlegg i " [:a {:href tech-forum-url} "#tech-forum"] " eller i tråder på " [:a {:href mikrobloggeriet-announce-url} "#mikrobloggeriet-announce"] ". "
       "Vi velger å skrive fordi vi " [:em "liker"] " å snakke om fag, og fordi vi bryr oss!"]

      [:p [:strong "For andre:"] " "
       "Mikrobloggeriet er et initiativ der teknologer fra " [:a {:href iterate-url} "Iterate"] " deler ting de bryr seg om i hverdagen. "
       "Vi velger å publisere fritt tilgjengelig på Internett fordi vi har tro på å dele kunnskap. "
       "Innhold og kode for Mikrobloggeriet ligger åpent på " [:a {:href github-olorm-url} "github.com/iterate/olorm"] ". "
       "Mikrobloggeriet kjører på " [:a {:href hops-url} "Headless Operations"] ". "
       "Hvis du har spørsmål eller kommentarer, kan du ta kontakt med " [:a {:href teodor-url} "Teodor"] "."]])))

(defn olorms-index [_req]
  (page/html5
   (into [:head]
         (shared-html-header))
   [:body
    [:p
     (roulette)]
    [:h1 "Alle OLORM-er"]
    [:table
     [:thead [:td "slug"]]
     [:tbody
      (for [olorm (olorm/docs {:repo-path ".."})]
        [:tr [:td
              [:a {:href (olorm/href olorm)} (:slug olorm)]]])]]]))

(def markdown->html
  "Convert markdown to html with pandoc and an in-memory cache"
  (iki/cache-fn-by iki/markdown->html identity))

(defn olorm->html [olorm]
  (when (olorm/exists? olorm)
    (markdown->html (slurp (olorm/index-md-path olorm)))))

(defn olorm [req]
  (tap> req)
  (let [olorm (olorm/->olorm {:slug (:slug (:route-params req))
                              :repo-path ".."})
        {:keys [number]} olorm]
    (tap> (olorm/->olorm olorm))
    (page/html5
     (into [:head] (shared-html-header))
     [:body
      [:p
       (roulette)
       " — "
       [:a {:href "/"} "mikrobloggeriet"]
       " "
       [:a {:href "/o/"} "o"]
       " — "
       (let [prev (olorm/->olorm {:number (dec number) :repo-path ".."})]
         (when (olorm/exists? prev)
           [:a {:href (olorm/href prev)} (:slug prev)]))
       " · "
       (let [prev (olorm/->olorm {:number (inc number) :repo-path ".."})]
         (when (olorm/exists? prev)
           [:a {:href (olorm/href prev)} (:slug prev)]))]
      (olorm->html olorm)])))

(defn jals->html [doc]
  (when (jals/exists? doc)
    (markdown->html (slurp (jals/index-md-path doc)))))

(defn jals [req]
  (let [doc {:slug (:slug (:route-params req))
             :repo-path ".."}]
    (page/html5
     (into [:head] (shared-html-header))
     [:body
      [:p [:a {:href "/"} ".."]]
      (jals->html doc)])))

(defn random-doc [_req]
  (let [target (or (when-let [random-olorm (olorm/random {:repo-path ".."})]
                     (olorm/href random-olorm))
                   "/")]
    {:status 307 ;; temporary redirect
     :headers {"Location" target}
     :body ""}))

(defroutes app
  (GET "/" req (index req))
  (GET "/health" _req {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})
  (GET "/vanilla.css" _req {:status 200 :headers {"Content-Type" "text/css"} :body (slurp "vanilla.css")})
  (GET "/mikrobloggeriet.css" _req {:status 200 :headers {"Content-Type" "text/css"} :body (slurp "mikrobloggeriet.css")})
  (GET "/o/" req (olorms-index req))
  (GET "/o/:slug/" req (olorm req))
  (GET "/j/:slug/" req (jals req))
  (GET "/random-doc" _req random-doc))

(defonce server (atom nil))
(def port 7223)
(defn stop-server [stop-fn] (when stop-fn (stop-fn)) nil)
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop! [] (swap! server stop-server))
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start! [_opts]
  (swap! server (fn [old-server]
                  (stop-server old-server)
                  (println (str "olorm.serve running: http://localhost:" port))
                  (httpkit/run-server #'app {:port port}))))
