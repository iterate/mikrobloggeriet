(ns mikrobloggeriet.db.migrate
  "Database migrations on Mikrobloggeriet"
  (:require
   [mikrobloggeriet.repl :as repl]
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
  "Delete the migrations table.

  Should `never` be used in production. Deleting the migrations table is useful
  _only_ when working on the migrations system."
  [conn]
  (assert (not-prod?) "Do NOT drop the migrations table in production.")
  (pg/query conn "drop table migrations"))



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
  "All migrations that have been used in the current session."
  [(sql-migration {:id "access-logs-table"
                   :up "create table access_logs (
                          id integer primary key,
                          method text,
                          uri text,
                          timestamp timestamp default current_timestamp,
                          info jsonb
                        )"
                   :down "drop table access_logs"})
   (sql-migration {:id "access-logs-table-2"
                   :up "drop table access_logs"
                   :down ""})
   (sql-migration {:id "access-logs-table"
                   :up "create table access_logs (
                          id serial primary key,
                          method text,
                          uri text,
                          timestamp timestamp default current_timestamp,
                          info jsonb
                        )"
                   :down "drop table access_logs"})

   ])

(def migration-index (ragtime/into-index all-migrations))

(def migrations
  "Represent the current desired database schema state.

  Should be equal to `all-migrations` when code is merged to master."
  [(sql-migration {:id "access-logs-table"
                   :up "create table access_logs (
                          id integer primary key,
                          method text,
                          uri text,
                          timestamp timestamp default current_timestamp,
                          info jsonb
                        )"
                   :down "drop table access_logs"})
   (sql-migration {:id "access-logs-table-2"
                   :up "drop table access_logs"
                   :down ""})
   (sql-migration {:id "access-logs-table"
                   :up "create table access_logs (
                          id serial primary key,
                          method text,
                          uri text,
                          timestamp timestamp default current_timestamp,
                          info jsonb
                        )"
                   :down "drop table access_logs"})])

(defn migrate-prod!
  "Migrate, or raise error on conflicts."
  [conn]
  (assert (= (count all-migrations) (count migrations))
          "Ensure we don't mess up migration state in production.")
  (ragtime/migrate-all (PgDatabase. conn)
                       migration-index
                       migrations
                       {:strategy ragtime.strategy/raise-error}))

(defn migrate-dev!
  "Migrate and rebase if necessary."
  [conn]
  (assert (not-prod?) "Do NOT apply migrations with rebasing in production.")
  (ragtime/migrate-all (PgDatabase. conn)
                       migration-index
                       migrations
                       {:strategy ragtime.strategy/rebase}))

(defn migrate! [conn env]
  (assert (#{:dev :prod} env) "Env must be dev or prod.")
  (cond (= :dev env) (migrate-dev! conn)
        (= :prod env) (migrate-prod! conn)
        :else (throw (ex-info "Illegal argument" {:env env
                                                  :legal-env-values #{:dev :prod}}))))

(comment
  (defonce dev-conn (:mikrobloggeriet.system/db @repl/state))
  (alter-var-root #'dev-conn (constantly (:mikrobloggeriet.system/db @repl/state)))
  (ensure-migrations-table! dev-conn)
  (dangerously-delete-migrations-table!! dev-conn)

  (pg/query dev-conn "select * from migrations")

  ;; in production, migrate with ragtime.strategy/raise-error
  (migrate-prod! (PgDatabase. dev-conn))

  ;; in development, migrate with ragtime.strategy/rebase
  (migrate-dev! (PgDatabase. dev-conn))

  :rcf)
