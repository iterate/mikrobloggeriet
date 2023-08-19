;; # Cohort: a missing Mikrobloggeriet abstraction

^{:nextjournal.clerk/visibility {:code :hide}}
(ns mikrobloggeriet.teodor.cohort
  (:require
   [mikrobloggeriet.cohort :as cohort]))

;; Currently, adding new cohorts requires adding lots of code.
;; We have several cohort specific namespaces:

[`mikrobloggeriet.olorm
 `mikrobloggeriet.olorm-cli
 `mikrobloggeriet.jals
 `mikrobloggeriet.jals-cli]

;; We want to make it easy to add new cohorts.
;;
;; We propose moving tools for working with cohorts `mikrobloggeriet.cohort`,
;; along with cohort data (what a cohort is about, who is in a cohort).
;;
;; This document explores the current state of the cohort api.

cohort/oj

(cohort/docs cohort/oj)

;; is this for use from the server, or from the CLI?
;; perhaps the CLI just needs to know where the repo is.
;; let's go for that.

;; let's try listing the public interface of olorm, then implement each function.

['->olorm
 'author-name
 'coerce
 'docs :DONE
 'exists?
 'git-user-email
 'href
 'index-md-path
 'infer-created-date
 'load-meta
 'md-skeleton
 'meta-path
 'next-number
 'olorms :DONE
 'path
 'random
 'save-meta!
 'today
 'uuid
 'validate]
