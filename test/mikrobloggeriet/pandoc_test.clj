(ns mikrobloggeriet.pandoc-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.pandoc :as pandoc]))

(deftest markdown->-test
  (let [markdown "# hei\n\npå deg!"]
    (is (map? (pandoc/markdown-> markdown)))
    (is (contains? (pandoc/markdown-> markdown)
                   :pandoc-api-version))))

(deftest convert-test
  (is (= "<p><em>teodor</em></p>" (-> "_teodor_" pandoc/markdown-> pandoc/->html str/trim))))

(deftest el->plaintext-test
  (is (= "hei du"
         (-> "hei _du_" pandoc/markdown-> :blocks first pandoc/el->plaintext))))

(deftest infer-title-test
  (let [doc "% ABOUT TIME

About time we got some shit done."]
    (is (= "ABOUT TIME"
           (-> doc pandoc/markdown-> pandoc/infer-title)))))
