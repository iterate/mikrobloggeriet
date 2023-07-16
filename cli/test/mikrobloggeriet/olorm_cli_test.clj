(ns mikrobloggeriet.olorm-cli-test
  (:require [mikrobloggeriet.olorm-cli :as olorm-cli]
            [clojure.test :refer [deftest testing is]]))

(def ci? (= "runner" (System/getenv "USER")))

(deftest repo-path-test
  (testing "A repo path is set"
    (is (some? (olorm-cli/repo-path)))))

(when-not ci?
  ;; none of these tests work under CI, because they assume a user has configured git user.name
  (deftest create-opts->commands-test
    (testing "we can generate commands without errors"
      (is (some? (olorm-cli/create-opts->commands {:dir (olorm-cli/repo-path) :git false :edit false}))))

    (testing "A seq of commands is returned, the first element of a command is a keyword"
      (let [commands (olorm-cli/create-opts->commands {:dir (olorm-cli/repo-path) :git false :edit false})]
        (doseq [c commands]
          (is (keyword? (first c))))))

    (testing "When we transform to a dry run, only printable commands are returned"
      (let [printable? (fn [command] (#{:prn :println} (first command)))
            commands (map olorm-cli/command->dry-command (olorm-cli/create-opts->commands {:dir (olorm-cli/repo-path) :git false :edit false}))]
        (doseq [c commands]
          (is (printable? c)))))

    (testing "When git is enabled, there is shelling out"
      (let [commands (olorm-cli/create-opts->commands {:dir (olorm-cli/repo-path) :git true :edit false})
            command-types (into #{} (map first commands))]
        (is (contains? command-types :shell))))

    (testing "When edit is enabled, there is shelling out"
      (let [commands (olorm-cli/create-opts->commands {:dir (olorm-cli/repo-path) :git false :edit true})
            command-types (into #{} (map first commands))]
        (is (contains? command-types :shell))))))
