(ns mikrobloggeriet.cohort
  (:require
   [datomic.api :as d]))

(defn href [cohort]
  (when-let [slug (:cohort/slug cohort)]
    (str "/" slug "/")))

(defn all [db]
  (for [cohort-id
        (d/q '[:find [?id ...]
               :where [_ :cohort/id ?id]]
             db)]
    (d/entity db [:cohort/id cohort-id])))

(defn route-all [cohort]
  (keyword (str "mikrobloggeriet." (:cohort/slug cohort))
           "all"))

(defn route-doc [cohort]
  (keyword (str "mikrobloggeriet." (:cohort/slug cohort))
           "doc"))
