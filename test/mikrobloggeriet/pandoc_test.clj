(ns mikrobloggeriet.pandoc-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.pandoc :as pandoc]))

(deftest markdown-test
  (testing "markdown-> output looks like sane pandoc json"
    (let [markdown "hei\npå deg!"]
      (is (map? (pandoc/markdown-> markdown)))
      (is (contains? (pandoc/markdown-> markdown)
                     :pandoc-api-version))))
  (testing "we can roundtrip from and to markdown"
    (let [markdown "hei\n\npå deg!\n"]
      (is (= markdown
             (-> markdown
                 pandoc/markdown->
                 pandoc/->markdown)))))
  (testing "but roundtripping only works exactly with a single trailing newline"
    (let [markdown-no-newline "hei\n\npå deg!"]
      (is (not= markdown-no-newline
                (-> markdown-no-newline
                    pandoc/markdown->
                    pandoc/->markdown))))
    (let [markdown-two-newlines "hei\n\npå deg!\n\n"]
      (is (not= markdown-two-newlines
                (-> markdown-two-newlines
                    pandoc/markdown->
                    pandoc/->markdown))))))

(deftest convert-test
  (is (= "<p><em>teodor</em></p>" (-> "_teodor_" pandoc/markdown-> pandoc/->html str/trim))))

(deftest el->plaintext-test
  (is (= "hei du"
         (-> "hei _du_" pandoc/markdown-> :blocks first pandoc/el->plaintext))))

(deftest title-test
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

(deftest org-test
  (testing "We can roundtrip text with org-mode"
    (let [org-text "hei /du/"]
      (is
       (= org-text
          (-> org-text
              pandoc/org->
              pandoc/->org
              str/trim)))))

  (testing "With org-standalone, we can keep title information"
    (let [title "THE BEST TITLE"]
      (is
       (= title
          (-> "hei _du_"
              pandoc/markdown->
              (pandoc/set-title title)
              (pandoc/->org-standalone)
              (pandoc/org->)
              pandoc/title))))))

(deftest h1-plaintext-test
  (is (= 1 1)))

(deftest h1-test
  (let [h1-el {:t "Header",
               :c
               [1
                ["super-duper-document" [] []]
                [{:t "Str", :c "super"}
                 {:t "Space"}
                 {:t "Str", :c "duper"}
                 {:t "Space"}
                 {:t "Str", :c "document"}]]}]
    (is (pandoc/h1? h1-el))
    (is (= "super duper document"
           (pandoc/header->plaintext h1-el)))))

(deftest infer-title-test
  (let [doc (pandoc/markdown-> "# super duper document")]
    (is (= "super duper document"
           (pandoc/infer-title doc)))))
