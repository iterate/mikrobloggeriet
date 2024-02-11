(ns mikrobloggeriet.db.migrate
  "Database migrations on Mikrobloggeriet"
  (:require
   [mikrobloggeriet.repl :as repl]
   [nextjournal.clerk :as clerk]
   [pg.core :as pg]
   [ragtime.protocols]))

(defn ensure-migrations-table!
  [conn]
  (pg/query conn "create table if not exists migrations (id text primary key)"))

(defn dangerously-delete-migrations-table!!
  [conn]
  (assert (not (System/getenv "HOPS_ENV")) "Do NOT drop the migrations table in production.")
  (pg/query conn "drop table migrations"))

(defonce dev-conn (:mikrobloggeriet.system/db @repl/state))

(comment
  (alter-var-root #'dev-conn (constantly (:mikrobloggeriet.system/db @repl/state)))
  (ensure-migrations-table! dev-conn)
  (dangerously-delete-migrations-table!! dev-conn)

  (pg/query dev-conn "select * from migrations")

  )


(defrecord PgDatabase [conn]
  ragtime.protocols/DataStore
  (add-migration-id [_ id]
    (pg/execute conn "insert into migrations(id) values ($1)" {:params [id]}))
  (remove-migration-id [_ id]
    (pg/execute conn "delete from migrations where id = $1", {:params [id]}))
  (applied-migration-ids [_]
    (->> (pg/query conn "select id from migrations")
         (map :id))))

^::clerk/no-cache
(clerk/caption "foo table"
               (clerk/table (pg/query dev-conn "select * from foo")))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
