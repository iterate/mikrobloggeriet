(ns mblog2.db-test
  (:require [clojure.test :refer [deftest is testing]]
            [mblog2.db :as db]))

(deftest loaddb-test
  (testing "Cohorts have names"
    (is (every? #(contains? % :cohort/name)
                (vals db/cohorts))))
  (testing "Cohorts have descriptions"
    (is (every? #(contains? % :cohort/description)
                (vals db/cohorts)))))
