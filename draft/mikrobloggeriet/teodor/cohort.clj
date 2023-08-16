;; # Cohort: a missing Mikrobloggeriet abstraction

(ns mikrobloggeriet.teodor.cohort)

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
