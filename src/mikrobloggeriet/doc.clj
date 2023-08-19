(ns mikrobloggeriet.doc)

(defn href
  "Create a link to a doc in a cohort"
  [cohort doc]
  (assert (:cohort/id cohort))
  (assert (:doc/slug doc))
  (str "/" (name (:cohort/id cohort)) "/" (:doc/slug doc)))
