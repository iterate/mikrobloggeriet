(ns mikrobloggeriet.cli-test
  (:require [mikrobloggeriet.cli :as cli]
            [clojure.test :refer [deftest testing is]]))

((deftest name-test
      (testing "no directory"
        (is (nil? (cli/create-opts->commands "" ""))))) )

