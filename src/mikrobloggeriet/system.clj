(ns mikrobloggeriet.system
  (:require
   [integrant.core :as ig]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.repl]
   [mikrobloggeriet.serve :as serve]
   [org.httpkit.server :as httpkit]))

(defmethod ig/init-key ::datomic
  [_ _]
  (db/loaddb db/cohorts db/authors))

(defmethod ig/init-key ::app
  [_ {:keys [datomic]}]
  (fn [req]
    (-> req
        (assoc ::datomic datomic)
        serve/app)))

(defmethod ig/init-key ::http-server
  [_ {:keys [port app]}]
  (assert (number? port) "")
  (httpkit/run-server app {:port port
                           :legacy-return-value? false}))

(defmethod ig/halt-key! ::http-server
  [_ server]
  (httpkit/server-stop! server))

(def default-config
  {::app {:datomic (ig/ref ::datomic)}
   ::http-server {:port 7223
                  :app (ig/ref ::app)}
   ::datomic {}})

(defn ^:export start! [overrides]
  (let [opts (cond-> default-config
                 (:port overrides)
                 (assoc-in [::http-server :port] (:port overrides)))
        system (ig/init opts)]
    (reset! mikrobloggeriet.repl/state system)
    system))

(comment
  (def sys2 (ig/init default-config))
  (ig/halt! sys2)
  ,)
