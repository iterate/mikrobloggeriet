(ns mikrobloggeriet.bubble
  (:refer-clojure :exclude [name])
  (:require
   [clojure.string :as str]
   [mikrobloggeriet.doc-template :as doc-template]))

(comment
  ;; example cohort:
  (sorted-map
   :cohort/root "text/oj"
   :cohort/slug "oj"
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}]))

;; See store.clj for available cohorts.

(defn slug [cohort]
  (:cohort/slug cohort))

(defn root [cohort]
  (:cohort/root cohort))

(defn members [cohort]
  (:cohort/members cohort))

(defn repo-path [cohort]
  (:cohort/repo-path cohort "."))

(defn set-repo-path [cohort repo-path]
  (assoc cohort :cohort/repo-path repo-path))

(defn name [cohort]
  (some-> (slug cohort) str/upper-case))

(defn index-md-template [cohort]
  (or (:cohort/index-md-template cohort)
      doc-template/md-quality-nudge))
