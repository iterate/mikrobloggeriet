(ns mikrobloggeriet.olav.olav-olav 
  (:require [mikrobloggeriet.olorm :as olorm]
            [mikrobloggeriet.pandoc]
            [nextjournal.clerk :as clerk]))

(+ 1 2)

(def olorms
  "List of all published olorms"
  (mikrobloggeriet.olorm/docs {:repo-path "."}))

;; (clerk/html (iki.api/markdown->html (slurp (olorm/index-md-path (nth olorms 5)))))
