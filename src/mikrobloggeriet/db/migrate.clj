(ns mikrobloggeriet.db.migrate
  "Database migrations on Mikrobloggeriet"
  (:require
   [mikrobloggeriet.repl :as repl]
   [pg.core :as pg]))

(defn ensure-migrations-table!
  [conn]
  )

(defn delete-migrations-table!
  [conn])

(defonce dev-conn (:mikrobloggeriet.system/db @repl/state))

(comment
  (alter-var-root #'dev-conn (constantly (:mikrobloggeriet.system/db @repl/state)))

  )

(pg/query dev-conn "select 123 as num")
