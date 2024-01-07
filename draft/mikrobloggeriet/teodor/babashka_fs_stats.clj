^{:nextjournal.clerk/toc true}
(ns mikrobloggeriet.teodor.babashka-fs-stats
  (:require
   [clojure.repl :as repl]
   [nextjournal.clerk :as clerk]))

;; ## Hjelpefunksjoner for å slå opp i navnerom

;; total:

(count (ns-interns 'babashka.fs))

;; whole ns:

(ns-interns 'babashka.fs)

;; ### Predicates for filtering vars

(defn var-private? [var]
  (= true (:private (meta var))))

(defn var-public? [var]
  (not (var-private? var)))

(defn var-fn? [var]
  (fn? (var-get var)))

(defn all-vars [ns-sym]
  (vals (ns-interns ns-sym)))

;; ## Statistikk

;; private funksjoner

(->> (all-vars 'babashka.fs)
     (filter var-private?)
     (filter var-fn?)
     count)

;; offentlige funksjoner

(let [query-babashka-fs (fn [pred]
                          (->> (all-vars 'babashka.fs)
                               (filter pred)
                               count))]
  {:private-fns (query-babashka-fs #(and (var-private? %) (var-fn? %)))
   :public-fns (query-babashka-fs #(and (var-public? %) (var-fn? %)))
   :non-fn-vars (query-babashka-fs #(not (var-fn? %)))})

;; det som ikke er funksjoner, hva er det for noe?

(->> (all-vars 'babashka.fs)
     (filter #(not (var-fn? %))))

(->> (all-vars 'babashka.fs)
     (filter #(not (var-fn? %)))
     (map var-get)
     (map type))

;; litt av hvert.

;; ### `clojure.lang.Delay`?

(->> (all-vars 'babashka.fs)
     (map var-get)
     (filter #(= clojure.lang.Delay (type %))))

;; objekter, ok.
;; Men hva er variabelnavneene?

(def delayed
  (->> (ns-interns 'babashka.fs)
       (filter (fn [[_sym v]]
                 (= clojure.lang.Delay (type (var-get v)))))))

;; la oss se på kildekoden.

(apply clerk/fragment
       (for [[sym _v] delayed]
         (let [qualified-sym (symbol "babashka.fs" (name sym))]
           (clerk/code (clojure.repl/source-fn qualified-sym)))))

;; OK, ser ut som diverse konstanter vi ikke vil laste _med en gang_.
;; Litt usikker på hvorfor.

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
