(ns mikrobloggeriet.teodor.morn
  (:require
   [clojure.string :as str]))

(defn morn [] (rand-nth '[🎉 ☕ 🌦]))
(morn)
;; => 🌦️

(morn)
;; => ☕️
