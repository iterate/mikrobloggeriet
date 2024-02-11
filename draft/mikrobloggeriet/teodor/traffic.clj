(ns mikrobloggeriet.teodor.traffic
  (:require
   [mikrobloggeriet.repl :as repl]
   [nextjournal.clerk :as clerk]
   [pg.core :as pg]))

(defonce conn (:mikrobloggeriet.system/db @repl/state))

(comment
  (alter-var-root #'conn (constantly (:mikrobloggeriet.system/db @repl/state)))

  )

(clerk/table
 (pg/query conn "select * from access_logs"))
