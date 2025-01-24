(ns mikrobloggeriet.ui.index
  (:require
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]))

(defn cohort-section [cohort]
  [:section
   [:h2 (:cohort/name cohort) " " [:a {:href (cohort/href cohort)} "↗"]]
   [:p (:cohort/description cohort)]
   [:ul {:class "doc-list"}
    (for [doc (->> (sort-by doc/number (:doc/_cohort cohort))
                   (remove :doc/draft?))]
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
