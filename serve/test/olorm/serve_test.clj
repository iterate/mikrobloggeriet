(ns olorm.serve-test
  (:require [olorm.serve :as serve]
            [clojure.test :refer [is testing deftest] ]
            [clojure.string :as str]))


(let
    ;; hack
    ;; please don't do this at home!

    ;; When we're running as a github action, we need to set the repo path before
    ;; starting
    ;;
    ;; A better way to solve this, would be to allow passing the repo path into the
    ;; command invocation. But we're not going to do that right now.
    [in-github-action? (and
                        (some? (System/getenv "GITHUB_ACTIONS"))
                        (= "/home/runner" (System/getenv "HOME"))
                        (= "runner" (System/getenv "USER")))]

  (when in-github-action?
    (spit "/home/runner/.config/olorm/config.edn" {:repo-path "/home/runner/work/olorm/olorm"})))

(deftest index-test
  (let [index (serve/index {})]
    (testing "An index was returned"
      (is (some? index)))

    (testing "Index looks like html"
      (is (and (string? index)
               (str/starts-with? index "<!DOCTYPE"))))

    (testing "Index refers to olorm-4"
      (is (str/includes? index "/o/olorm-4")))

    (testing "olorm 4 html contains more than 500 chars"
      (is (< 500 (count (:body (serve/olorm {:route-params {:slug "olorm-4"}}))))))))
