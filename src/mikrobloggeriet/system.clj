(ns ^{:experimental true
      :doc "Try out using Integrant for system state"}
    mikrobloggeriet.system
  (:require
   [integrant.core :as ig]
   [mikrobloggeriet.config :as config]
   [mikrobloggeriet.serve :as serve]
   [org.httpkit.server :as httpkit]))

(defn dev
  "Development system without db"
  []
  {::app {:recreate-routes :every-request}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}})

(defn prod
  "Production system without db"
  []
  {::app {:recreate-routes :once}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}})

(defmethod ig/init-key ::app
  [_ {:keys [recreate-routes] :as opts}]
  (assert (#{:every-request :once} recreate-routes)
          "App must be recreated either on every request, or once.")
  ;; no db to attach
  (cond (= recreate-routes :every-request)
        (fn [req]
          (let [app (serve/app)]
            (app req)))
        (= (:recreate-routes opts) :once)
        (let [app (serve/app)]
          (fn [req]
            (app req)))))

(defmethod ig/init-key ::http-server
  [_ {:keys [port app]}]
  (println (str "mikrobloggeriet.serve running: http://localhost:" port))
  (httpkit/run-server app {:port port}))

(defmethod ig/halt-key! ::http-server
  [_ http-server-halt-fn]
  (http-server-halt-fn))

(comment
  (def sys2 (ig/init (dev)))
  (ig/halt! sys2)
  ,)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start-prod! [_opts]
  (ig/init (prod)))
