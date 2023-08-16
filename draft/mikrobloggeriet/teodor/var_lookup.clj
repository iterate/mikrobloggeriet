(ns mikrobloggeriet.teodor.var-lookup
  (:require
   [clojure.set :as set]
   [clojure.string :as str]))

;; I want to iterate over my namespaces and look for vars with certain metadata.

(defn lookup-vars [namespaces ns-match? var-match?]
  (->> namespaces
       (filter ns-match?)
       (mapcat ns-interns)
       (map val)
       (filter var-match?)
       (sort-by symbol)))

^{:nextjournal.clerk/auto-expand-results? true}
(lookup-vars (all-ns)
             #(str/starts-with? % "clojure")
             (comp :deprecated meta))

^{:nextjournal.clerk/auto-expand-results? true}
(lookup-vars (all-ns)
             #(str/starts-with? % "clojure")
             (comp :deprecated meta))

#_
(let [allns-set (into #{} (map symbol (all-ns)))
      loadedlibs-set (into #{} (loaded-libs))]
  (count (set/intersection allns-set loadedlibs-set)))

(defn allns-syms []
  (map (comp symbol ns-name)
       (all-ns)))

^{:nextjournal.clerk/auto-expand-results? true}
(take 10 (sort (allns-syms)))

^{:nextjournal.clerk/auto-expand-results? true}
(take 10 (sort (loaded-libs)))

(count (all-ns))
(count (loaded-libs))


(str/starts-with? 'clojure "clojure")

(->> (loaded-libs)
     (filter #(str/starts-with? % "java")))

(->> (allns-syms)
     (filter #(str/starts-with? % "java")))
