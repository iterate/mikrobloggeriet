(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.store :as store]))

(deftest slug-test
  (is (= "oj" (cohort/slug store/oj)))
  (is (nil? (cohort/slug {}))))

(deftest name-test
  (is (= "OLORM"
         (cohort/name store/olorm))))
