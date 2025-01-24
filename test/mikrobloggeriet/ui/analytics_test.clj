(ns mikrobloggeriet.ui.analytics-test
  (:require [clojure.test :refer [deftest is]]
            [mikrobloggeriet.ui.analytics :as analytics]))

(deftest public-analytics-routes
  (is (contains? analytics/public-analytics-route-names
                 :mikrobloggeriet.olorm/all))
  (is (contains? analytics/public-analytics-route-names
                 :mikrobloggeriet.olorm/doc)))
