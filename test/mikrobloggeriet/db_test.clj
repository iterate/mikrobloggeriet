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
    (let [db (db/loaddb {:cohorts db/cohorts :authors db/authors})]
      (testing "olorm funker fjell"
        (is (= (d/entity db [:cohort/id :cohort/olorm])
               (:doc/cohort (d/entity db [:doc/slug "olorm-12"])))))

      (testing "leik har også kohort!"
        (is (some? (:doc/cohort (d/entity db [:doc/slug "leik-3"]))))))))
