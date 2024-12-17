(ns mikrobloggeriet.repl)

(defonce state (atom nil))

(comment
  (def db (:mikrobloggeriet.system/datomic @state))
  (require '[datomic.api :as d])


  (d/q '[:find ?slug
         :where [_ :doc/slug ?slug]]
       db)


  (def olorm-12 (d/entity db [:doc/slug "olorm-99"]))

  (def oj (d/entity db [:cohort/id :cohort/oj]))
  (require '[mblog2.db :as db])
  ()

  (keys olorm-12)
  :rcf)


