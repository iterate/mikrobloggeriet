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

(clerk/table
 (pg/query conn "
select
  method,
  uri,
  date_trunc('hour', timestamp) as timestamp_hour,
  count(*)
from access_logs
group by method, uri, timestamp_hour
"))


^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
