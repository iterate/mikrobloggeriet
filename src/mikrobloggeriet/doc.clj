(ns mikrobloggeriet.doc
  (:require
   [babashka.fs :as fs]))

(defn href
  "Create a link to a doc in a cohort"
  [cohort doc]
  (when (and (:cohort/id cohort)
             (:doc/slug doc))
    (str "/" (name (:cohort/id cohort)) "/" (:doc/slug doc))))

(defn exists? [cohort doc]
  (and (:cohort/root cohort)
       (:doc/slug doc)
       (fs/directory? (fs/file (:cohort/root cohort)
                               (:doc/slug doc)))
       (fs/exists? (fs/file (:cohort/root cohort)
                            (:doc/slug doc)
                            "meta.edn"))
       (fs/exists? (fs/file (:cohort/root cohort)
                            (:doc/slug doc)
                            "index.md"))))
