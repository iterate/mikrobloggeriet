(ns mikrobloggeriet.style
  (:require
   [clojure.string :as str]))

(defn inline [style]
  (str/join "; "
            (for [[k v] style]
              (str (name k) ": " v))))

(comment
  (inline {:display "flex"})
  "display: flex;"

  (inline {:display "flex" :margin-left "12px"})

  )
