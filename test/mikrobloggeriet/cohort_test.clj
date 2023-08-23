(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.cohort :as cohort]))

(deftest docs-test
  (testing "we find olorm-3"
    (is (contains? (->> (cohort/docs cohort/olorm)
                        (map :doc/slug)
                        (into #{}))
                   "olorm-3")))

  (testing "we find jals-4"
    (is (contains? (->> (cohort/docs cohort/jals)
                        (map :doc/slug)
                        (into #{}))
                   "jals-4"))))

(deftest slug-test
  (is (= "oj" (cohort/slug cohort/oj)))
  (is (nil? (cohort/slug {}))))
