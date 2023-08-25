(ns mikrobloggeriet.cli-test
  (:require [mikrobloggeriet.cli :as cli]
            [clojure.test :refer [deftest testing is]]
            [clojure.string :as str]))

(defn- git-command? [cmd]
  (let [[_cmd _opts command-str] cmd]
    (str/starts-with? command-str "git")))

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

  (testing "When git is enabled, there are generated commands for shelling out"
    (is (contains? (->> (cli/create-opts->commands {:dir "." :git true :edit false})
                        (map first)
                        (into #{}))
                   :shell)))

  (testing "git-command? helper works as expected"

    (is (git-command? [:shell {:dir "."} "git add ."]))
    (is (not (git-command? [:shell {:dir "."} "vim file"])))
    (is (some git-command?
              [[:shell {:dir "."} "git add ."]
               [:shell {:dir "."} "vim file"]]))
    (is (not (some git-command?
                   [[:shell {:dir "."} "vim file"]]))))

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
