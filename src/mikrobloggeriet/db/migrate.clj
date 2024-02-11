(ns mikrobloggeriet.db.migrate
  "Database migrations on Mikrobloggeriet"
  (:require
   [mikrobloggeriet.repl :as repl]
   [nextjournal.clerk :as clerk]
   [pg.core :as pg]
   [ragtime.core :as ragtime]
   [ragtime.strategy]
   [ragtime.protocols]))

(defn ensure-migrations-table!
  [conn]
  (pg/query conn "
create table if not exists migrations(
  id text primary key,
  timestamp timestamp default current_timestamp
)"))

(defn- not-prod? []
  (not (System/getenv "HOPS_ENV")))

(defn dangerously-delete-migrations-table!!
  [conn]
  (assert (not-prod?) "Do NOT drop the migrations table in production.")
  (pg/query conn "drop table migrations"))

(comment
  (defonce dev-conn (:mikrobloggeriet.system/db @repl/state))
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

(defn sql-migration [{:keys [id up down]}]
  (reify ragtime.protocols/Migration
    (id [_] id)
    (run-up! [_ db] (pg/query (.conn db) up))
    (run-down! [_ db] (pg/query (.conn db) down))))


(def all-migrations
  [(sql-migration {:id "add-foo-table"
                   :up "create table foo (id integer primary key, description text)"
                   :down "create table foo (id integer primary key, description text)"})
   (sql-migration {:id "add-bar-table"
                   :up "create table bar (id integer)"
                   :down "drop table bar"})])

(def migration-index (ragtime/into-index all-migrations))

(def migrations
  [(sql-migration {:id "add-foo-table"
                   :up "create table foo (id integer primary key, description text)"
                   :down "create table foo (id integer primary key, description text)"})])

(defn migrate-dev!
  "Migrate and rebase if necessary."
  [conn]
  (assert (not-prod?) "Do NOT apply migrations with rebasing in production.")
  (ragtime/migrate-all (PgDatabase. conn)
                       migration-index
                       migrations
                       {:strategy ragtime.strategy/rebase}))

(defn migrate!
  "Migrate, or raise error on conflicts."
  [conn]
  (ragtime/migrate-all (PgDatabase. conn)
                       migration-index
                       migrations
                       {:strategy ragtime.strategy/raise-error}))

(comment
  (defonce dev-conn (:mikrobloggeriet.system/db @repl/state))

  ;; in production, migrate with ragtime.strategy/raise-error
  (migrate! (PgDatabase. dev-conn))

  ;; in development, migrate with ragtime.strategy/rebase
  (migrate-dev! (PgDatabase. dev-conn))

  :rcf)

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
