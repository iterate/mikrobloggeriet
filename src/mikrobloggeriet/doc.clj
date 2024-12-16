(ns mikrobloggeriet.doc
  (:require
   [clojure.string :as str]))

(defn ^{:deprecated true}
  from-slug [slug]
  {:doc/slug slug})

(defn slug [doc]
  (:doc/slug doc))

(defn number [doc]
  (when-let [slug (:doc/slug doc)]
    (parse-long (last (str/split slug #"-")))))

(defn href [cohort doc]
  (when (and (:cohort/slug cohort)
             (:doc/slug doc))
    (str "/" (:cohort/slug cohort)
         "/" (:doc/slug doc)
         "/")))
