(ns mikrobloggeriet.db
  (:require
   [mikrobloggeriet.config :as config]
   [pg.core :as pg]
   [malli.core :as m]
   [malli.transform :as mt]
   [clojure.pprint :refer [pprint]]))

(def Pg2Config
  [:map
   [:host :string]
   [:port :int]
   [:user :string]
   [:password :string]
   [:database :string]])

(def dev-config
  {:host "localhost"
   :port config/pg-port
   :user "mikrobloggeriet"
   :password "mikrobloggeriet"
   :database "mikrobloggeriet"})

(comment
  (m/validate Pg2Config (dissoc dev-config :host))
  ;; => false

  (m/validate Pg2Config dev-config)

  (m/coerce Pg2Config (assoc dev-config :port "9000") mt/string-transformer)
  ;; => {:host "localhost",
  ;;     :port 9000,
  ;;     :user "mikrobloggeriet",
  ;;     :password "mikrobloggeriet",
  ;;     :database "mikrobloggeriet"}


  )

;; igrishaev/pg2 config docs: https://github.com/igrishaev/pg2?tab=readme-ov-file#connecting-to-the-server
;;
;; HOPS PG env vars: https://hops-doc.app.iterate.no/reference/environment_variables.html

(def hops->pg2
  {"PGDATABASE" :database
   "PGHOST" :host
   "PGPASSWORD" :password
   "PGPORT" :port
   "PGUSER" :user})

(defn hops-config [env]
  (let [raw-config
        (into {}
              (for [[envvar confkey] hops->pg2]
                [confkey (get env envvar)]))]
    ;; Ensures :port is string
    (m/coerce Pg2Config raw-config mt/string-transformer)))

(comment
  (hops-config {"PGDATABASE" "mikrobloggeriet"
                "PGHOST" "localhost"
                "PGPASSWORD" "mikrobloggeriet"
                "PGPORT" config/pg-port
                "PGUSER" "mikrobloggeriet"})
  ;; => {:database "mikrobloggeriet",
  ;;     :host "localhost",
  ;;     :password "mikrobloggeriet",
  ;;     :port 7574,
  ;;     :user "mikrobloggeriet"}

  (System/getenv))

(defn try-db-stuff [_req]
  ;; function to try running database stuff to see if we can connect successfully.
  (try
    (with-open [conn (pg/connect (hops-config (System/getenv)))]
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body (with-out-str (pprint (pg/query conn "select 42 as fourty_two")))})
    (catch Exception e
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body (with-out-str (pprint {:exception e
                                    :message (.getMessage e)}))})))

(defn trydb-2 [req]
  (if-let [conn (:mikrobloggeriet.system/db req)]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (with-out-str (pprint (pg/query conn "select 42 as fourty_two")))}
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body "No DB available."}))
