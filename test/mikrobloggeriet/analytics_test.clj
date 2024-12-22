(ns mikrobloggeriet.analytics-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.analytics :as analytics]
   [time-literals.read-write])
  (:import
   [java.time Instant]))

;; (time-literals.read-write/print-time-literals-clj!)

(deftest rf-test
  (is (= (analytics/rf {"/urlog/" {"2024-12-22" 22}}
                       {:uri "/urlog/"
                        :mikrobloggeriet.system/now  (Instant/parse "2024-12-22T18:20:08.542820Z")})
         {"/urlog/" {"2024-12-22" 23}}))

  (is (= (analytics/rf nil
                       {:uri "/urlog/"
                        :mikrobloggeriet.system/now (Instant/parse "2024-12-22T18:20:08.542820Z")})
         {"/urlog/" {"2024-12-22" 1}})))
