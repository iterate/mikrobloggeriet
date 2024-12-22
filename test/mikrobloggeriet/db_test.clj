(ns mikrobloggeriet.db-test
  (:require [clojure.test :refer [deftest is testing]]
            [datomic.api :as d]
            [mikrobloggeriet.db :as db]))

(deftest cohort-test
  (testing "Cohorts have names"
    (doseq [cohort (vals db/cohorts)]
      (is (contains? cohort :cohort/name))))
  (testing "Cohorts have descriptions"
    (doseq [cohort (vals db/cohorts)]
      (is (contains? cohort :cohort/description)))))

(deftest doc-test
  (testing "Docs have cohorts"
    (let [db (db/loaddb {:cohorts db/cohorts :authors db/authors})
          olorm-12 (d/entity db [:doc/slug "olorm-12"])
          olorm (d/entity db [:cohort/id :cohort/olorm])]
      (is (= olorm
             (:doc/cohort olorm-12))))))
