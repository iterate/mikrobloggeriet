(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.db :as db]))

(deftest href-test
  (is (= "/urlog/"
         (cohort/href {:cohort/slug "urlog"})))
  (testing "Returnerer nil hvis vi ikke kjenner slug"
    (is (nil? (cohort/href {})))))

(def db (db/loaddb {:cohorts db/cohorts :authors db/authors}))

(deftest all-test
  (is (contains? (->> (cohort/all db)
                      (map :cohort/id)
                      (into #{}))
                 :cohort/olorm)))
