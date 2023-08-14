(ns mikrobloggeriet.johan
  (:require [clojure.string :as str]))

(+ 10 2)

(if true
  "Denne skal printes"
  "Denne skal ikke printes")

(if false
  "Denne skal ikke printes"
  "Denne skal printes")

(* (if false 1 2)
   10)


;; if (true) { console.log(1) } else {console.log(2)}

(if true (do (prn 1)1) (do (prn 2)))

(if true
  (do (println "Success!")
      (println "Denne skal bryte opp")
      (println (+ 2 3))
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))

(when true
  (println "Success!")
  "Aba cadabra")

(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The incredible Bulk"])

failed-protagonist-names

(println failed-protagonist-names)

(:fruit 2)

(defn is-it-apple
  [fruit]
  (str "The fruit you have given me is "
       (if (= fruit :Apple)
         "an apple"
         "not an apple")))

(is-it-apple :Apple)
(is-it-apple :Pear)

(def capital
  (hash-map :Norway "Oslo" :Sweden "Stockholm" :Denmark "Cobenhagen")
  )

(get capital :Norway )

(def country
  (hash-map :Norway {:population 5400000 :area 400000 :capital "Oslo"} 
            :Sweden (hash-map :population 7000000 :area 400000 :capital "Stockholm")
            :Denmark (hash-map :population 5000000 :area 100000 :capital "Cobenhagen")))

(get country :Norway)
(get-in country [:Denmark :capital])
(country :Norway)
(:Norway capital)

(def countries
  (vector "Albania" "Argentina" "Armenia" "Asarbadjan"))
(get countries 3)
(conj countries "Bangladesh")

'(1 2 3 4 5)

(hash-set 1 3 2 4)

(def names
  (hash-set :a "Johan" :b "Johan" :c "Johan" :d "Olav" ))

(get names :a)

(+ 1 2 3 4)

(or - +)
