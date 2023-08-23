(ns mikrobloggeriet.doc-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]))

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

(deftest number-test
  (is (= 1 (doc/number {:doc/slug "olorm-1"})))
  (is (= 42 (doc/number {:doc/slug "jals-42"}))))
