(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.cohort.markdown :as cohort.markdown]
   [mikrobloggeriet.store :as store]))

(deftest slug-test
  (is (= "oj" (cohort.markdown/slug store/oj)))
  (is (nil? (cohort.markdown/slug {}))))

(deftest name-test
  (is (= "OLORM"
         (cohort.markdown/name store/olorm))))

(deftest href-test
  (is (= "/urlog/"
         (cohort/href {:cohort/slug "urlog"}))))
