(ns ^{:experimental true
      :doc "Try out using Integrant for system state"}
    mikrobloggeriet.system
  (:require
   [integrant.core :as ig]
   [malli.core :as m]
   [mikrobloggeriet.config :as config]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.serve :as serve]
   [org.httpkit.server :as httpkit]
   [pg.core :as pg]))

(defn dev+db
  "Development system with db"
  []
  {::db {:host "localhost"
         :port config/pg-port
         :user "mikrobloggeriet"
         :password "mikrobloggeriet"
         :database "mikrobloggeriet"}
   ::app {:recreate-routes :every-request
          :db (ig/ref ::db)}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}})

(defn dev
  "Development system without db"
  []
  {::app {:recreate-routes :every-request}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}})

(defn prod+db
  "Production system with db"
  []
  {::db (db/hops-config (System/getenv))
   ::app {:recreate-routes :once
          :db (ig/ref ::db)}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}})

(defn prod
  "Production system without db"
  []
  {::app {:recreate-routes :once}
   ::http-server {:port config/http-server-port
                  :app (ig/ref ::app)}})

(defmethod ig/init-key ::db
  [_ opts]
  (let [db-conf (m/coerce db/Pg2Config opts)]
    (pg/connect db-conf)))

(defmethod ig/halt-key! ::db
  [_ conn]
  (pg/close conn))

(defmethod ig/init-key ::app
  [_ {:keys [recreate-routes db] :as opts}]
  (assert (#{:every-request :once} recreate-routes)
          "App must be recreated either on every request, or once.")
  (if db
    ;; we got a db, attach it.
    (cond (= recreate-routes :every-request)
          (fn [req]
            ((serve/app) (assoc req ::db db)))
          (= (:recreate-routes opts) :once)
          (let [app (serve/app)]
            (fn [req] (app (assoc req ::db db)))))
    ;; no db to attach, fine, don't attach a db.
    (cond (= recreate-routes :every-request)
          (fn [req]
            ((serve/app) req))
          (= (:recreate-routes opts) :once)
          (serve/app))))

(defmethod ig/init-key ::http-server
  [_ {:keys [port app]}]
  (println (str "mikrobloggeriet.serve running: http://localhost:" port))
  (httpkit/run-server app {:port port}))

(defmethod ig/halt-key! ::http-server
  [_ http-server-halt-fn]
  (http-server-halt-fn))

(comment
  (def sys2 (ig/init (dev+db)))
  (ig/halt! sys2)

  (pg/query (::db sys2) "select 43 as fourty_two")


  )
