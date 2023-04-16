(ns olorm.serve
  (:require
   [compojure.core :refer [defroutes GET]]
   [hiccup.page :as page]
   [iki.api :as iki]
   [olorm.devui :as devui]
   [olorm.lib :as olorm]
   [org.httpkit.server :as httpkit]))

(defn index [_req]
  (page/html5
   [:head (hiccup.page/include-css "/vanilla.css")]
   [:body
    [:h1 "OLORM"]]))

(def markdown->html
  (iki/cache-fn-by iki/markdown->html identity))

(defn olorm->html [olorm]
  (when (olorm/exists? olorm)
    (markdown->html (slurp (olorm/index-md-path olorm) ))))

(defn olorm [req]
  (reset! devui/xx req) ;; i want to use tap> here
  (let [olorm (select-keys (:route-params req) [:slug])]
    (page/html5
     [:head (hiccup.page/include-css "/vanilla.css")]
     [:body
      (olorm->html (assoc olorm :repo-path ".."))])))

(defroutes app
  (GET "/" req (index req))
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
