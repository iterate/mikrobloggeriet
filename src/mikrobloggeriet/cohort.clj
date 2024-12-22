(ns mikrobloggeriet.cohort
  (:require
   [datomic.api :as d]))

(defn href [cohort]
  (when-let [slug (:cohort/slug cohort)]
    (str "/" slug "/")))

(defn all-cohorts [db]
  (for [cohort-id
        (d/q '[:find [?id ...]
               :where [_ :cohort/id ?id]]
             db)]
    (d/entity db [:cohort/id cohort-id])))
