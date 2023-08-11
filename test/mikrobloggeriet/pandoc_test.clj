(ns mikrobloggeriet.pandoc-test
  (:require [clojure.test :refer [deftest is]]
            [mikrobloggeriet.pandoc :as pandoc]
            [clojure.string :as str]))

(deftest markdown->-test
  (is (map? (pandoc/markdown-> "# hei\n\npå deg!")))
  (is (contains? (pandoc/markdown-> "# hei\n\npå deg!")
                 :pandoc-api-version)))

(deftest convert-test
  (is (= "<p><em>teodor</em></p>" (-> "_teodor_" pandoc/markdown-> pandoc/->html str/trim))))

(-> "hei _du_" pandoc/markdown-> :blocks first pandoc/el->plaintext)
(-> "hei _du_" pandoc/markdown-> :blocks first)

(deftest el->plaintext-test
  (is (= "hei du"
         (-> "hei _du_" pandoc/markdown-> :blocks first pandoc/el->plaintext))))
