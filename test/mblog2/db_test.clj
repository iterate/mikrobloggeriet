(ns mblog2.db-test
  (:require [clojure.test :refer [deftest is testing]]
            [mblog2.db :as db]))

(deftest cohort-test
  (testing "Cohorts have names"
    (doseq [cohort (vals db/cohorts)]
      (is (contains? cohort :cohort/name))))
  (testing "Cohorts have descriptions"
    (doseq [cohort (vals db/cohorts)]
      (is (contains? cohort :cohort/description)))))
