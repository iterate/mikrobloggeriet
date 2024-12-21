(ns mikrobloggeriet.cohort)

(defn href [cohort]
  (when-let [slug (:cohort/slug cohort)]
    (str "/" slug "/")))
