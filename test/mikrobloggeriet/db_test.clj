(ns mikrobloggeriet.db-test
  (:require [clojure.test :refer [deftest is testing]]
            [datomic.api :as d]
            [mikrobloggeriet.db :as db]))

(deftest valid-cohort-data
  (doseq [cohort (vals db/cohorts)]
    (is (contains? cohort :cohort/name))
    (is (contains? cohort :cohort/description))
    (is (contains? cohort :cohort/root))))

(deftest doc-test
  (testing "Docs have cohorts"
    (let [db (db/loaddb {:cohorts db/cohorts :authors db/authors})
          olorm-12 (d/entity db [:doc/slug "olorm-12"])
          olorm (d/entity db [:cohort/id :cohort/olorm])]
      (is (= olorm
             (:doc/cohort olorm-12))))))
