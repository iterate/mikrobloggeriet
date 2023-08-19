(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.cohort :as cohort]))

(deftest docs-test
  (testing "we find olorm-3"
    (is (contains? (->> (cohort/docs cohort/olorm)
                        (map :slug)
                        (into #{}))
                   "olorm-3")))

  (testing "we find jals-4"
    (is (contains? (->> (cohort/docs cohort/jals)
                        (map :slug)
                        (into #{}))
                   "jals-4"))))

(deftest href-test
  (is (= "/olorm/olorm-1"
         (cohort/href cohort/olorm
                      {:doc/slug "olorm-1"}))))
