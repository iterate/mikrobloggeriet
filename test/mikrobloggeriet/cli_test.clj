(ns mikrobloggeriet.cli-test
  (:require [mikrobloggeriet.cli :as cli]
            [clojure.test :refer [deftest testing is]]))

(deftest create-opts->commands-test
  (testing "we can generate commands without errors"
    (is (some? (cli/create-opts->commands {:dir "." :git false :edit false})))))

(deftest learn-test
  (testing "some? returns true for all non-nil values"
    (is (some? 42))
    (is (some? false))
    (is (not (some? nil))))

  (testing "nil values are falsey"
    (is (not nil)))

  (testing "false is falsey"
    (is (not false))))
