(ns mikrobloggeriet.cache-test
  (:require [clojure.test :refer [deftest is]]
            [mikrobloggeriet.cache :as cache]))

(deftest cache-fn-by-test
  (is (= ((cache/cache-fn-by (fn [a b] (+ a b))
                             (fn [a b] (str a " " b)))
          1 2)
         3)))
