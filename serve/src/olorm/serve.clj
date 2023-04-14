(ns olorm.serve
  (:require
   [hiccup.page :as page]
   [compojure.core :refer [defroutes GET]]
   [org.httpkit.server :as httpkit]))

(defn index [_req]
  (page/html5
   [:head (hiccup.page/include-css "/vanilla.css")]
   [:body
    [:h1 "OLORM"]]))

(defroutes app
  (GET "/" req (index req))
  (GET "/vanilla.css" _req {:status 200 :headers {"Content-Type" "text/css"} :body (slurp "vanilla.css")}))

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
