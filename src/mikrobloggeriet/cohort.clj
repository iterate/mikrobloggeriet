(ns mikrobloggeriet.cohort
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
                    {:author/first-name "Olav"}])
  )

;; This namespace contains no constructors.
;; See store.clj for available cohorts.

(defn slug [cohort]
  (or
   (:cohort/slug cohort)
   ;; TODO: delete this branch, present for backwards compatibility
   (when-let [cohort-id (:cohort/id cohort)]
     (clojure.core/name cohort-id))))

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
