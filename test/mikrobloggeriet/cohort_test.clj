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
  #_#_#_
  (testing "we find jals-4")
  (contains? )
  (is (= true
         (cohort/foo))))
