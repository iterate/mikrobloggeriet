(ns mikrobloggeriet.pandoc-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.pandoc :as pandoc]))

(deftest markdown->-test
  (is (map? (pandoc/markdown-> "# hei\n\npå deg!")))
  (is (contains? (pandoc/markdown-> "# hei\n\npå deg!")
                 :pandoc-api-version)))

(deftest convert-test
  (is (= "<p><em>teodor</em></p>" (-> "_teodor_" pandoc/markdown-> pandoc/->html str/trim))))

(deftest el->plaintext-test
  (is (= "hei du"
         (-> "hei _du_" pandoc/markdown-> :blocks first pandoc/el->plaintext))))
