(ns mikrobloggeriet.teodor.api2.cohort
  "A cohort is a set of people writing toghether")

(comment
  ;; example cohort:
  (sorted-map
   :cohort/root "text/oj"
   :cohort/slug "oj"
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}])
  )

;; This namespace contains no constructors.
;; See store.clj for available cohorts.

(defn slug [cohort]
  (:cohort/slug cohort))

(defn root [cohort]
  (:cohort/root cohort))

(defn members [cohort]
  (:cohort/members cohort))
