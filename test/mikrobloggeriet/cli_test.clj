(ns mikrobloggeriet.cli-test
  (:require [mikrobloggeriet.cli :as cli]
            [clojure.test :refer [deftest testing is]]
            [clojure.string :as str]
            [mikrobloggeriet.cohort :as cohort]))

(defn- git-command? [cmd]
  (let [[cmd _opts command-str] cmd]
    (and (= cmd :shell)
         (string? command-str)
         (str/starts-with? command-str "git"))))

(deftest create-opts->commands-test
  (let [sample-opts {:dir "."
                     :git true
                     :editor "vim"
                     :cohort-id :olorm
                     :git.user/email "user@example.com"}]
    
    (testing "we can generate commands without errors"
      (is (some? (cli/create-opts->commands {:dir "."
                                             :git true
                                             :editor "vim"
                                             :cohort-id :olorm
                                             :git.user/email "user@example.com"}))))

    (testing "A seq of commands is returned, all commands are prints"
      (is (every? keyword?
                  (->> (cli/create-opts->commands {:dir "."
                                                   :git true
                                                   :editor "vim"
                                                   :cohort-id :olorm
                                                   :git.user/email "user@example.com"})
                       (map first))))))

  (testing "When we transform to a dry run, only printable commands are returned"
    (is (every? #{:prn :println}
                (->> (cli/create-opts->commands {:dir "."
                                                 :git true
                                                 :editor "vim"
                                                 :cohort-id :olorm
                                                 :git.user/email "user@example.com"})
                     (map cli/command->dry-command)
                     (map first)))))

  (testing "When git is enabled, there are generated commands for shelling out"
    (is (contains? (->> (cli/create-opts->commands {:dir "."
                                                    :git true
                                                    :editor "vim"
                                                    :cohort-id :olorm
                                                    :git.user/email "user@example.com"})
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

  (testing "When git is enabled, there are git commands"
    (let [commands (cli/create-opts->commands {:dir "."
                                               :git true
                                               :editor "vim"
                                               :cohort-id :olorm
                                               :git.user/email "user@example.com"})]
      (is (some git-command? commands))))

  (testing "When git is disabled, there are no git commands."
    (let [commands (cli/create-opts->commands {:dir "."
                                               :git false
                                               :editor "vim"
                                               :cohort-id :olorm
                                               :git.user/email "user@example.com"})]
      (is (not (some git-command? commands)))))

  (testing "When git is enabled, there are git commands"
    (let [editor "vim"
          commands (cli/create-opts->commands {:dir "."
                                               :git true
                                               :editor editor
                                               :cohort-id :olorm
                                               :git.user/email "user@example.com"})]
      (is (some (fn [[_cmd _opts shell-command-str]]
                  (when (string? shell-command-str)
                    (str/starts-with? shell-command-str editor)))
                commands))))

  (testing "when shelling out, the dir argument is used"
    (let [dir "/tmp/mikrobloggeriet-test"
          commands (cli/create-opts->commands {:dir dir
                                               :git true
                                               :editor "vim"
                                               :cohort-id :olorm
                                               :git.user/email "user@example.com"})
          shell-commands (filter (fn [[cmd _ _]]
                                   (= cmd :shell))
                                 commands)]
      (is
       (every? (fn [[_ opts _]]
                 (= dir (:dir opts)))
               shell-commands)))))

(deftest learn-test
  (testing "some? returns true for all non-nil values"
    (is (some? 42))
    (is (some? false))
    (is (not (some? nil))))

  (testing "nil values are falsey"
    (is (not nil)))

  (testing "false is falsey"
    (is (not false)))

  (testing "concat"
    (is (= [1 2 3 4]
           (concat [1 2] [3 4])))

    (is (= [1 2 3 4 \5 \6]
           (concat (when false [1 2 3])
                   [1 2]
                   ""
                   nil
                   [3 4]
                   "56"))))

  (testing "tread last"
    (is (= '(1 3 5)
           (filter odd? (range 6))
           (->> (range 6)
                (filter odd?))))

    (is (= '(10 30 50)

           (map (partial * 10)
                (filter odd? (range 6)))

           (->> (range 6)
                (filter odd? ,,,)
                (map (partial * 10) ,,,))

           (->> (range 6)
                (filter odd?)
                (map (fn [x] (* 10 x))))

           (->> (range 6)
                (filter odd?)
                (map #(* 10 %)))))
    )
  )

