(ns mikrobloggeriet.ui.cohort-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [datomic.api :as d]
            [mikrobloggeriet.db :as db]
            [mikrobloggeriet.ui.cohort :as ui.cohort]))

(deftest doc-table-test
  (let [db (db/loaddb {:cohorts db/cohorts :authors db/authors})
        olorm (d/entity db [:cohort/id :cohort/olorm])
        response (ui.cohort/doc-table db olorm {})]
    (is (map? response))
    (let [{:keys [body]} response]
      (is (str/includes? body "Alle OLORM-er"))
      (is (str/includes? body "publisert"))
      (is (str/includes? body "olorm-13")))))
