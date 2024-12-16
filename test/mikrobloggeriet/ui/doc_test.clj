(ns mikrobloggeriet.ui.doc-test
  (:require [clojure.test :refer [deftest testing is]]
            [mblog2.db :as db]
            [datomic.api :as d]
            [mikrobloggeriet.ui.doc :as ui.doc]))

(deftest page-test
  (let [db (db/loaddb db/cohorts db/authors)
        oj-2 (d/entity db [:doc/slug "oj-2"])]
    (is (map? (ui.doc/page oj-2)))))

#_(deftest doc-test
    (let [app (serve/app)]
  ;; Sanity test that one document for each cohort renders successfully. Makes it more comfortable to work with doc logic!
      (let [olorm-1 (app {:uri "/olorm/olorm-1/" :request-method :get})]
        (is (str/includes? (str/lower-case (:body  olorm-1))
                           "søvn")
            "OLORM-1 handler om viktigheten av en god natts søvn."))

      (let [jals-1 (app {:uri "/jals/jals-1/" :request-method :get})]
        (is (str/includes? (str/lower-case (:body jals-1))
                           "modeller")
            "JALS-1 handler maskinlæringsmodeller."))

      (let [oj-1 (app {:uri "/oj/oj-1/" :request-method :get})]
        (is (str/includes? (str/lower-case (:body oj-1))
                           "refaktorering")
            "OJ-1 handler refaktorering."))))

