(ns mikrobloggeriet.cli-test
  (:require [mikrobloggeriet.cli :as cli]
            [clojure.test :refer [deftest testing is]]))

(deftest name-test
  (testing "no directory"
    (is (nil? (cli/create-opts->commands {:dir ""
                                          :git ""
                                          :edit ""})))))

(deftest learn-test
  (testing "some? returns true for all non-nil values"
    (is (some? 42))
    (is (some? false))
    (is (not (some? nil))))

  (testing "nil values are falsey"
    (is (not nil)))

  (testing "false is falsey"
    (is (not false))))
