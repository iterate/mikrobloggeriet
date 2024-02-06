(ns mikrobloggeriet.olav.colors
  (:require [mikrobloggeriet.contrast :as colors]
            [nextjournal.clerk :as clerk]))

(clerk/html
 (let [colors (colors/generate-contrasting-colors 4.5)]
   [:svg {:width 500 :height 100}
    [:circle {:cx  25 :cy 50 :r 25 :fill (:color-1 colors)}]
    [:circle {:cx  100 :cy 50 :r 25 :fill (:color-2 colors)}]]))


^::clerk/refresh


(comment
  (clerk/clear-cache!))

 