(ns mikrobloggeriet.doc-test
  (:require
   [clojure.test :refer [deftest is]]
   [datomic.api :as d]
   [mblog2.db :as db]
   [mikrobloggeriet.doc :as doc]))

(deftest number-test
  (is (= 1 (doc/number {:doc/slug "olorm-1"})))
  (is (= 42 (doc/number {:doc/slug "jals-42"})))
  (is (nil? (doc/number {:doc/slug "YO-PEEPS"}))))

(deftest doc-href-test
  (is (= "/olorm/olorm-13/"
         (doc/href {:cohort/slug "olorm"}
                   {:doc/slug "olorm-13"}))))

(deftest previous-test
  (let [db (db/loaddb db/cohorts db/authors)]
    (is (= "olorm-1"
           (:doc/slug (doc/previous db (d/entity db [:doc/slug "olorm-2"])))))))

(deftest next-test
  (let [db (db/loaddb db/cohorts db/authors)]
    (is (= "olorm-3"
           (:doc/slug (doc/next db (d/entity db [:doc/slug "olorm-2"])))))))
