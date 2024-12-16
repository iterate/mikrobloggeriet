(ns mikrobloggeriet.ui
  (:require
   [mikrobloggeriet.doc :as doc]))

(defn cohort-section [cohort]
  [:section
   [:h2 (:cohort/name cohort)]
   [:p (:cohort/description cohort)]
   [:ul {:class "doc-list"}
    (for [doc (sort-by doc/number (:cohort/docs cohort))]
      [:li [:a {:href (doc/href cohort doc)}
            (:doc/slug doc)]])]])

(comment
  (set! *print-namespace-maps* false)
  (require '[datomic.api :as d])
  (def db (:mikrobloggeriet.system/datomic @mikrobloggeriet.repl/state))
  (def olorm (d/entity db [:cohort/id :cohort/olorm]))
  (into {} olorm)
  :rcf)
