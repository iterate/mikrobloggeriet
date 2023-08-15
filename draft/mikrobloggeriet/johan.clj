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

(map inc [0 1 2 3 4])
(map conj ["hei" "heii" "heiii"])

(defn highest-number
  "Should return the highest number"
  ([numbers index high] 
   (if (>= index (count numbers))
     high
     (let [current (get numbers index)]
       (if (> current high)
         (highest-number numbers (inc index) current)
         (highest-number numbers (inc index) high))))
   )
  ([numbers index]
   (highest-number numbers index (first numbers))) 
  ([numbers]
   (highest-number numbers 0)))


(highest-number [1 2 3 4 5 6 7 8 9 10])

(def syntax-test 
  {:keys [1 2 3 4 5]})

println(syntax-test)

(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])
(defn matching-part 
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)}
  )

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))

(def x "hei")
x
(def noe 
  (println x))
  