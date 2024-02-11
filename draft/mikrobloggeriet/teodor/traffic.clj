(ns mikrobloggeriet.teodor.traffic
  (:require
   [mikrobloggeriet.repl :as repl]
   [pg.core :as pg]))

(defonce conn (:mikrobloggeriet.system/db @repl/state))

(pg/query conn "select * from access_logs")
