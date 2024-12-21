(ns ^{:experimental true
      :doc "Try out using Integrant for system state"}
    mikrobloggeriet.system
  (:require
   [integrant.core :as ig]
   [mblog2.db :as db]
   [mikrobloggeriet.config :as config]
   [mikrobloggeriet.repl]
   [mikrobloggeriet.serve :as serve]
   [org.httpkit.server :as httpkit]))

(defn dev
  "Development system without db"
  []
  {::app {:recreate-routes :every-request
          :datomic (ig/ref ::datomic)}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}
   ::datomic {}})

(defmethod ig/init-key ::datomic
  [_ _]
  (db/loaddb db/cohorts db/authors))

(defn prod
  "Production system without db"
  []
  {::app {:recreate-routes :every-request
          :datomic (ig/ref ::datomic)}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}
   ::datomic {}})

(defmethod ig/init-key ::app
  [_ {:keys [recreate-routes datomic]}]
  (assert (#{:every-request :once} recreate-routes)
          "App must be recreated either on every request, or once.")
  ;; no db to attach
  (cond (= recreate-routes :every-request)
        (fn [req]
          (let [app (serve/app)]
            (app (-> req
                     (assoc ::datomic datomic)))))

        (= recreate-routes :once)
        (let [app (serve/app)]
          (fn [req]
            (app (-> req
                     (assoc ::datomic datomic)))))))

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

(defn ^:export start! [overrides]
  (set! *print-namespace-maps* false)
  (let [opts (cond-> (prod)
               (:port overrides)
               (assoc-in [::http-server :port] (:port overrides)))
        system (ig/init opts)]
    (reset! mikrobloggeriet.repl/state system)
    system))

(defn ^{:export true
        :deprecated "Use start! instead."}
  start-prod! [extra-opts]
  (let [opts (cond-> (prod)
               (:port extra-opts)
               (assoc-in [::http-server :port] (:port extra-opts)))
        system (ig/init opts)]
    (reset! mikrobloggeriet.repl/state system)
    system))
