(ns mikrobloggeriet.repl)

(defonce state (atom nil))

(comment
  (def db (:mikrobloggeriet.system/datomic @state))
  (require '[datomic.api :as d])

  (d/q '[:find ?slug
         :where [_ :doc/slug ?slug]]
       db)
  :rcf)


