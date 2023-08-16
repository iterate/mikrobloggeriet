(ns mikrobloggeriet.cohort
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.jals :as jals]
   [mikrobloggeriet.olorm :as olorm]))

(def ^:private cohorts-by-id
  {:olorm {}
   :jals {}})

(defn docs
  ([]
   (docs {}))
  ([opts]
   (let [repo-path (fs/canonicalize (or (:repo-path opts) "."))
         cohort (:cohort opts)]
     (assert (fs/directory? repo-path) ":repo-path must be a directory.")
     (assert (contains? (into #{nil} (keys cohorts-by-id)) cohort)
             "Cohort must be a know cohort, or nil")
     (case cohort
       :olorm (olorm/docs {:repo-path repo-path})
       :jals (jals/docs {:repo-path repo-path})
       (concat (olorm/docs {:repo-path repo-path})
               (jals/docs {:repo-path repo-path}))))))
