(ns mikrobloggeriet.teodor.olorm-draw)

;; trekke tilfeldig olorm-skriver fra pool

rand-nth ; trekk tilfeldig fra "sekvens"
vec      ; lag vektor av noe
"olr"    ; en clojure string
\c       ; en clojure character

(type "olr")
(type \c)

(vec "olr")

[1 2 3]
(list 1 2 3)

(def m1
  {:o "Oddmund"
   :r "Richard"
   :l "Lars"})

(def m2
  {\o "Oddmund"
   \r "Richard"
   \l "Lars"})

(keyword (str \c))

(let [pool "olr"]
  (get m1
       (keyword (str (rand-nth (vec pool))))))

(let [pool "olr"]
  (get m2 (rand-nth (vec pool))))

(defn num->str [n]
  (cond (= n 11) "THe eleventh"
        (= n 9000) "Something large!"
        :else (str "Number: " n)))

(num->str 10)

(let [pool "olrt"
      first-letter->name {\o "Oddmund"
                          \r "Richard"
                          \l "Lars"}]
  (get first-letter->name (rand-nth pool)))

(sequential? [])
(sequential? ())
(sequential? {})
(seqable? {})

(seqable? [])
(seqable? "")

(get [:a :b :c] 1 "fant ingenting")
(get {:a "aaa" :b "bbbbbb"} :c "fant ingeting")

nth
