(ns olorm.serve
  (:require
   [compojure.core :refer [defroutes GET]]
   [hiccup.page :as page]
   [iki.api :as iki]
   [olorm.lib :as olorm]
   [clojure.pprint]
   [org.httpkit.server :as httpkit]))

(defn index [_req]
  (let [olorm-announce-url "https://garasjen.slack.com/archives/C05355N5TCL"
        github-olorm-url "https://github.com/iterate/olorm/"
        tech-forum-url "https://garasjen.slack.com/archives/C2K35RDNJ"
        teodor-url "https://teod.eu/"
        hops-url "https://www.headless-operations.no/"
        iterate-url "https://www.iterate.no/"]
    (page/html5
     [:head (hiccup.page/include-css "/vanilla.css")]
     [:body
      [:h1 "OLORM"]
      [:p
       (interpose " · "
                  (for [olorm (olorm/olorms {:repo-path ".."})]
                    [:span
                     [:a {:href (olorm/href olorm)} (:slug olorm)]]))]

      [:hr]

      [:p [:strong "For Iterate-ansatte:"] " "
       "Diskuter gjerne på OLORM-er i " [:a {:href tech-forum-url} "#tech-forum"] " eller i tråder på " [:a {:href olorm-announce-url} "#olorm-announce"] ". "
       "Vi velger å skrive fordi vi " [:em "liker"] " å snakke om fag, og fordi vi bryr oss!"]

      [:p [:strong "For andre:"] " "
       "OLORM er et initiativ der teknologer fra " [:a {:href iterate-url} "Iterate"] " deler ting de bryr seg om i hverdagen. "
       "Vi velger å publisere fritt tilgjengelig på Internett fordi vi har tro på å dele kunnskap. "
       "Innhold og kode for OLORM ligger åpent på " [:a {:href github-olorm-url} "github.com/iterate/olorm"] ". "
       "OLORM kjører på " [:a {:href hops-url} "Headless Operations"] ". "
       "Hvis du har spørsmål eller kommentarer, kan du ta kontakt med " [:a {:href teodor-url} "Teodor"] "."]])))

(def markdown->html
  (iki/cache-fn-by iki/markdown->html identity))

(defn olorm->html [olorm]
  (when (olorm/exists? olorm)
    (markdown->html (slurp (olorm/index-md-path olorm)))))

(defn olorm [req]
  (tap> req)
  (let [olorm {:slug (:slug (:route-params req))
               :repo-path ".."}]
    (page/html5
     [:head (hiccup.page/include-css "/vanilla.css")]
     [:body
      [:p [:a {:href "/"} ".."]]
      (olorm->html olorm)])))

(defroutes app
  (GET "/" req (index req))
  (GET "/health" _req {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})
  (GET "/vanilla.css" _req {:status 200 :headers {"Content-Type" "text/css"} :body (slurp "vanilla.css")})
  (GET "/o/:slug/" req (olorm req)))

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
