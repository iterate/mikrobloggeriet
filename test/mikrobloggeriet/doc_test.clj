(ns mikrobloggeriet.doc-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.doc :as doc]))

(deftest number-test
  (is (= 1 (doc/number {:doc/slug "olorm-1"})))
  (is (= 42 (doc/number {:doc/slug "jals-42"})))
  (is (nil? (doc/number {:doc/slug "YO-PEEPS"}))))
