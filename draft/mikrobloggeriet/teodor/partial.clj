(ns mikrobloggeriet.teodor.partial)

(defn my-partial [f & args]
  (fn [& more-args]
    (apply f (concat args more-args))))

(def politely (my-partial str "Please, Dear Sir, "))
(def politely2 (fn [args] (apply str "Please, Dear Sir, " args)))

(politely "can you pass me the butter?")
(politely2 "can you pass me the butter?")

(defn partial1 [f x]
  (fn [y]
    (f x y)))

((partial1 * 10)
 11)

((partial * 10 20)
 11)

(* 1 2 3)

((partial * 1 2 3)
 4 5 6)

(* 1 2 3 4 5 6)

((partial + 1 2 3)
 4 5 6)

(+ 1 2 3 4 5 6)
