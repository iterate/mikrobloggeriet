(ns mikrobloggeriet.db
  (:require
   [mikrobloggeriet.config :as config]
   [pg.core :as pg]
   [clojure.pprint :refer [pprint]]))

(def dev-config
  {:host "localhost"
   :port config/pg-port
   :user "mikrobloggeriet"
   :password "mikrobloggeriet"
   :database "mikrobloggeriet"})

;; igrishaev/pg2 config docs: https://github.com/igrishaev/pg2?tab=readme-ov-file#connecting-to-the-server
;;
;; HOPS PG env vars: https://hops-doc.app.iterate.no/reference/environment_variables.html

(def hops->pg2
  {"PGDATABASE" :database
   "PGHOST" :host
   "PGPASSWORD" :password
   "PGPORT" :port
   "PGUSER" :user})

(def pg2->hops
  (into {}
        (for [[k v] hops->pg2]
          [v k])))

(defn hops-config [env]
  (into {}
        (for [[envvar confkey] hops->pg2]
          [confkey (get env envvar)])))

(comment
  (hops-config {"PGDATABASE" "mikrobloggeriet"
                "PGHOST" "localhost"
                "PGPASSWORD" "mikrobloggeriet"
                "PGPORT" config/pg-port
                "PGUSER" "mikrobloggeriet"})

  (System/getenv))

(defn try-db-stuff [_req]
  ;; function to try running database stuff to see if we can connect successfully.
  (let [pg-config (hops-config (System/getenv))
        conn (pg/connect pg-config)
        fourty-two (pg/query conn "select 42 as fourty_two")
        response
        {:status 200
         :headers {"Content-Type" "text/plain"}
         :body (with-out-str (pprint fourty-two))}]
    (pg/close conn)
    response))
