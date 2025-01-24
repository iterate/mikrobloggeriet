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

(deftest routes
  (testing "all docs for a cohort"
    (is (= :mikrobloggeriet.olorm/all
           (cohort/route-all {:cohort/slug "olorm"}))))
  (testing "one doc in a cohort"
    (is (= :mikrobloggeriet.olorm/doc
           (cohort/route-doc {:cohort/slug "olorm"})))))
