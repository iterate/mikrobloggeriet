(ns mikrobloggeriet.pandoc-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.pandoc :as pandoc]))

(deftest markdown->-test
  (testing "markdown-> output looks like sane pandoc json"
    (let [markdown "# hei\n\npå deg!"]
      (is (map? (pandoc/markdown-> markdown)))
      (is (contains? (pandoc/markdown-> markdown)
                     :pandoc-api-version))))
  (testing "we can roundtrip from and to markdown"
    (let [markdown "# hei\n\npå deg!\n"]
      (is (= markdown
             (-> markdown
                 pandoc/markdown->
                 pandoc/->markdown))))))

(deftest convert-test
  (is (= "<p><em>teodor</em></p>" (-> "_teodor_" pandoc/markdown-> pandoc/->html str/trim))))

(deftest el->plaintext-test
  (is (= "hei du"
         (-> "hei _du_" pandoc/markdown-> :blocks first pandoc/el->plaintext))))

(deftest infer-title-test
  (let [doc "% ABOUT TIME

About time we got some shit done."]
    (is (= "ABOUT TIME"
           (-> doc pandoc/markdown-> pandoc/title)))))

(deftest standalone-is-required-to-keep-metadata
  (testing "Without standalone, title is lost"
      (let [title "The Great Title"]
        (is (nil? (-> "A great document of great items."
                      (pandoc/markdown->)
                      (pandoc/set-title title)
                      (pandoc/->markdown)
                      (pandoc/markdown->)
                      (pandoc/title))))))

  (testing "With standalone, title is kept."
    (testing "for markdown"
      (let [title "The Great Title"]
        (is (= title
               (-> "A great document of great items."
                   (pandoc/markdown->)
                   (pandoc/set-title title)
                   (pandoc/->markdown-standalone)
                   (pandoc/markdown->)
                   (pandoc/title))))))
    (testing "for html"
      (let [title "The Great Title"]
        (is (= title
               (-> "A great document of great items."
                   (pandoc/markdown->)
                   (pandoc/set-title title)
                   (pandoc/->html-standalone)
                   (pandoc/html->)
                   (pandoc/title))))))))
