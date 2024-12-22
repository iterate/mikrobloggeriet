(ns mikrobloggeriet.ui.doc-test
  (:require [clojure.test :refer [deftest is]]
            [datomic.api :as d]
            [mikrobloggeriet.db :as db]
            [mikrobloggeriet.ui.doc :as ui.doc]))

(deftest page-test
  (let [db (db/loaddb {:cohorts db/cohorts :authors db/authors})
        oj-2 (d/entity db [:doc/slug "oj-2"])]
    (is (map? (ui.doc/page oj-2 {})))))

