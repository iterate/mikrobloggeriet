(ns mblog.indigo-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.indigo :as indigo]))

(deftest left-bar
  (is
   (contains?
    (->> [{:doc/title "Unminifying av kode med LLM"
           :doc/markdown "lang tekst"}]
         (indigo/innhold->hiccup)
         (tree-seq seqable?
                   identity)
         set)
    "Unminifying av kode med LLM")))
