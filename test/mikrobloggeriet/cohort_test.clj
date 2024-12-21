(ns mikrobloggeriet.cohort-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.cohort :as cohort]))

(deftest href-test
  (is (= "/urlog/"
         (cohort/href {:cohort/slug "urlog"})))
  (testing "Returnerer nil hvis vi ikke kjenner slug"
    (is (nil? (cohort/href {})))))
