(ns mikrobloggeriet.teodor.morn
  (:require
   [clojure.string :as str]))

(defn morn [] (rand-nth (mapv symbol (str/split "☕️ 😎 🌦️" #" "))))
(morn)
;; => 🌦️

(morn)
;; => ☕️
