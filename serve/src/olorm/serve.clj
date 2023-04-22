(ns olorm.serve
  (:require
   [compojure.core :refer [defroutes GET]]
   [hiccup.page :as page]
   [iki.api :as iki]
   [olorm.lib :as olorm]
   [clojure.pprint]
   [org.httpkit.server :as httpkit]))

(defn index [_req]
  (page/html5
   [:head (hiccup.page/include-css "/vanilla.css")]
   [:body
    [:h1 "OLORM"]
    [:pre (with-out-str (clojure.pprint/pprint (olorm/olorms {:repo-path ".."})))]
    [:ul [:li "trala"]]
    ]))

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
      [:p [:a {:href "/"} "alle OLORM-er"]]
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
