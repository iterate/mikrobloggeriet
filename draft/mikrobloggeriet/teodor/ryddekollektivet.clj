(ns mikrobloggeriet.teodor.ryddekollektivet
  (:require
   [clojure.string :as str]))

;; Hvordan finner vi deprekerte vars?
;;
;; En løsning er med kode!

(defn lookup-vars [namespaces ns-match? var-match?]
  (->> namespaces
       (filter ns-match?)
       (mapcat ns-interns)
       (map val)
       (filter var-match?)
       (sort-by symbol)))
​
(lookup-vars (all-ns)
             (fn [n]
               (str/starts-with? (str (ns-name n)) "mikrobloggeriet"))
             (fn [v]
               (:deprecated (meta v))))
