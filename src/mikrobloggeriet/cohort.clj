(ns mikrobloggeriet.cohort
  (:require
   [mikrobloggeriet.cohort.markdown :as cohort.markdown]))

;; generic cohort interface

(defmulti slug :cohort/type)
(defmethod slug :default [_] nil)
(defmethod slug :cohort.type/markdown
  [cohort]
  (cohort.markdown/slug cohort))
(defmethod slug :cohort.type/urlog
  [_]
  "urlog")

(comment
  (require 'mikrobloggeriet.store)
  (slug mikrobloggeriet.store/urlog)
  (slug mikrobloggeriet.store/olorm)

  )
