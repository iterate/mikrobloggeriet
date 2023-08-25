(ns mikrobloggeriet.cli-test
  (:require [mikrobloggeriet.cli :as cli]
            [clojure.test :refer [deftest testing is]]))

(deftest create-opts->commands-test
  (testing "we can generate commands without errors"
    (is (some? (cli/create-opts->commands {:dir "." :git false :edit false}))))

  (testing "A seq of commands is returned, all commands are prints"
    (is (every? keyword?
                (->> (cli/create-opts->commands {:dir "." :git false :edit false})
                     (map first)))))

  (testing "When we transform to a dry run, only printable commands are returned"
    (is (every? #{:prn :println}
                (->> (cli/create-opts->commands {:dir "."
                                                 :git false
                                                 :edit false})
                     (map cli/command->dry-command)
                     (map first)))))

  (testing "When git is enabled, there is shelling out"
    (is (contains? (->> (cli/create-opts->commands {:dir "." :git true :edit false})
                        (map first)
                        (into #{}))
                   :shell)))

  )



(deftest learn-test
  (testing "some? returns true for all non-nil values"
    (is (some? 42))
    (is (some? false))
    (is (not (some? nil))))

  (testing "nil values are falsey"
    (is (not nil)))

  (testing "false is falsey"
    (is (not false))))
