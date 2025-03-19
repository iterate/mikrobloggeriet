(ns mblog.leik-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.leik :as leik]))

(deftest lager-riktig-slug
  (is (= "leik-1"
         (-> (leik/load-doc "text/leik/1.md")
             :doc/slug)))

  )
