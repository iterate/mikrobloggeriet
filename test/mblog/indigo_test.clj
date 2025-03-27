(ns mblog.indigo-test
  (:require
   [clojure.test :refer [deftest is]]
   [mblog.indigo :as indigo]))

(deftest lazyload-images
  (is (= [:div [:img {:loading "lazy"}]]
         (indigo/lazyload-images [:div [:img]])))

  (is (= [:div [:img {:loading "lazy"} "body"]]
         (indigo/lazyload-images [:div [:img "body"]])))

  (is (= [:div [:img {:loading "lazy" :class "lol"} "body"]]
         (indigo/lazyload-images [:div [:img {:class "lol"} "body"]]))))

(deftest left-bar
  (is
   (contains?
    (->> {:docs [{:doc/title "Unminifying av kode med LLM"
                  :doc/markdown "lang tekst"}]}
         (indigo/innhold->hiccup)
         (tree-seq seqable?
                   identity)
         set)
    "Unminifying av kode med LLM")))
