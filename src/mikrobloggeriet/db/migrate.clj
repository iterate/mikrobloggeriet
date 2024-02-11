(ns mikrobloggeriet.db.migrate
  "Database migrations on Mikrobloggeriet"
  (:require
   [mikrobloggeriet.repl :as repl]
   [pg.core :as pg]
   [nextjournal.clerk :as clerk]))

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

(pg/query dev-conn "create table if not exists foo(id integer not null, description text)")

(comment
  (pg/execute dev-conn "insert into foo(id, description) values ($1, $2)" {:params [1 "first"]})


  )

^::clerk/no-cache
(clerk/table (pg/query dev-conn "select * from foo"))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
