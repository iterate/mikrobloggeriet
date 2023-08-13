(ns mikrobloggeriet.serve-test
  (:require [mikrobloggeriet.serve :as serve]
            [clojure.test :refer [is testing deftest] ]
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
      (is (str/includes? index "/o/olorm-4")))

    (testing "olorm 4 html contains more than 500 chars"
      (is (< 500 (count (:body (serve/olorm {:route-params {:slug "olorm-4"}}))))))))
