(ns mikrobloggeriet.cohort)

(defn href [cohort]
  (str "/" (:cohort/slug cohort) "/"))
