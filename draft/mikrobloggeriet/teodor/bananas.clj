(ns mikrobloggeriet.teodor.bananas
  (:require
   [mikrobloggeriet.repl :as repl]
   [nextjournal.clerk :as clerk]
   [pg.core :as pg]))


(defonce conn (:mikrobloggeriet.system/db @repl/state))

(comment
  (alter-var-root #'conn (constantly (:mikrobloggeriet.system/db @repl/state)))

  )

(clerk/table
 (pg/query conn "select * from bananas_sold"))

(comment
  (pg/query conn "insert into bananas_sold (banana_seller, banana_buyer, banana_url) values ('Jim Bean', 'James Bond', 'https://banana.com/1')")

  )


(->> (pg/query conn "select count(id) as banana_count from bananas_sold")
     first
     :banana_count)


(defn query-one [conn sql]
  (->> (pg/query conn sql)
       first
       vals
       first))

(query-one conn "select count(id) as banana_count from bananas_sold")

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
