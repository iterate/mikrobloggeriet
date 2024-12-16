(ns mikrobloggeriet.doc-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.doc :as doc]
  [mblog2.db :as db]
  [datomic.api :as d]))

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
    (is (doc/previous db (d/entity db [:doc/slug "olorm-2"])))))

(deftest next-test
  (let [db (db/loaddb db/cohorts db/authors)]
    (is (doc/next db (d/entity db [:doc/slug "olorm-2"])))))
