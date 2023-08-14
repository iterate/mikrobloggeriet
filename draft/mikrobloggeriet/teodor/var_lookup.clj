(ns mikrobloggeriet.teodor.var-lookup
  (:require
   [clojure.string :as str]))

;; I want to iterate over my namesapces and look for vars with certain metadata.

(defn lookup-vars [ns-match? var-match?]
  (->> (all-ns)
       (filter ns-match?)
       (mapcat ns-interns)
       (map val)
       (filter var-match?)
       (sort-by symbol)))

^{:nextjournal.clerk/auto-expand-results? true}
(let [deprecated-vars (lookup-vars #(str/starts-with? % "clojure")
                                   (comp :deprecated meta))]
  [deprecated-vars
   (meta (first deprecated-vars))])
