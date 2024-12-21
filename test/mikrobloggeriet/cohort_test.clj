(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.cohort :as cohort]))

(deftest href-test
  (is (= "/urlog/"
         (cohort/href {:cohort/slug "urlog"}))))
