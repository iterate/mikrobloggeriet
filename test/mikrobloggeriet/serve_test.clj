(ns mikrobloggeriet.serve-test
  (:require [mikrobloggeriet.serve :as serve]
            [clojure.test :refer [is testing deftest]]
            [clojure.string :as str]))

(deftest index-test
  (let [index-resp (serve/index {})
        index (:body index-resp)]
    (testing "An index was returned"
      (is (some? index)))

    (testing "Index looks like html"
      (is (and (string? index)
               (str/starts-with? index "<!DOCTYPE"))))

    (testing "Index refers to olorm-4"
      (is (str/includes? index "/olorm/olorm-4")))))

(deftest doc-test
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
