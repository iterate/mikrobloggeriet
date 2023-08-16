(ns mikrobloggeriet.teodor
  (:require [mikrobloggeriet.johan :as jonny] ))

jonny/failed-protagonist-names

(def x 33)

(def point {:x 33 :y 44})

point

(get point :x)
(get point :y)

:x

:mild

:johan

(let [severity :mild]
  (cond (= severity :mild) "don't stress about it"
        (= severity :serious) "lots of stress!!!"))