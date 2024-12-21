(ns ^{:experimental true
      :doc "Try out using Integrant for system state"}
    mikrobloggeriet.system
  (:require
   [integrant.core :as ig]
   [mikrobloggeriet.config :as config]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.repl]
   [mikrobloggeriet.serve :as serve]
   [org.httpkit.server :as httpkit]))

(defn dev
  "Development system without db"
  []
  {::app {:datomic (ig/ref ::datomic)}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}
   ::datomic {}})

(defmethod ig/init-key ::datomic
  [_ _]
  (db/loaddb db/cohorts db/authors))

(defn prod
  "Production system without db"
  []
  {::app {:datomic (ig/ref ::datomic)}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}
   ::datomic {}})

(defmethod ig/init-key ::app
  [_ {:keys [datomic]}]
  (fn [req]
    (-> req
        (assoc ::datomic datomic)
        serve/app)))

(defmethod ig/init-key ::http-server
  [_ {:keys [port app]}]
  (httpkit/run-server app {:port port
                           :legacy-return-value? false}))

(defmethod ig/halt-key! ::http-server
  [_ server]
  (httpkit/server-stop! server))

(comment
  (def sys2 (ig/init (dev)))
  (ig/halt! sys2)
  ,)

(defn ^:export start! [overrides]
  (let [opts (cond-> (prod)
               (:port overrides)
               (assoc-in [::http-server :port] (:port overrides)))
        system (ig/init opts)]
    (reset! mikrobloggeriet.repl/state system)
    system))
