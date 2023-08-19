(ns mikrobloggeriet.doc-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [babashka.fs :as fs]))

(deftest href-test
  (is (= "/olorm/olorm-1"
         (doc/href cohort/olorm
                   {:doc/slug "olorm-1"}))))

(deftest exists?-test
  (is (doc/exists? cohort/olorm
                   {:doc/slug "olorm-1"})))

(deftest index-md-path-test
  (let [md-file (doc/index-md-path cohort/olorm {:doc/slug "olorm-1"})]
    (is (= "o/olorm-1/index.md"
           (str md-file)))))
