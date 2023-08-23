(ns mikrobloggeriet.doc
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]))

;; I wonder if we should change all these function signatures from
;;
;;     (fn [cohort doc] ,,,)
;;
;; to
;;
;;     (fn [doc cohort] ,,,)
;;
;; in order to take the object first.
;; I think this will help make threading easier.

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

(defn href
  "Create a link to a doc in a cohort"
  [cohort doc]
  (when (and (:cohort/id cohort)
             (:doc/slug doc))
    (str "/" (name (:cohort/id cohort)) "/" (:doc/slug doc))))

(defn index-md-path [cohort doc]
  (when (exists? cohort doc)
    (fs/file (:cohort/root cohort)
             (:doc/slug doc)
             "index.md")))

(defn number [doc]
  (when-let [slug (:doc/slug doc)]
    (parse-long (last (str/split slug #"-")))))
