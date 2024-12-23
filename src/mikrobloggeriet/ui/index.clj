(ns mikrobloggeriet.ui.index
  (:require
   [mikrobloggeriet.doc :as doc]))

(defn cohort-section [cohort]
  [:section
   [:h2 (:cohort/name cohort) " " [:a {:href (:cohort/slug cohort)} "↗"]]
   [:p (:cohort/description cohort)]
   [:ul {:class "doc-list"}
    (for [doc (sort-by doc/number (:doc/_cohort cohort))]
      [:li [:a {:href (doc/href doc)}
            (:doc/slug doc)]])]])

(comment
  (set! *print-namespace-maps* false)
  (require '[datomic.api :as d])
  (require 'mikrobloggeriet.state)
  (def db mikrobloggeriet.state/datomic)
  (def olorm (d/entity db [:cohort/id :cohort/olorm]))
  (into {} olorm)
  :rcf)
