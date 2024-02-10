(ns mikrobloggeriet.teodor.unike-skrivere
  (:require
   [clojure.string :as str]
   [mikrobloggeriet.store :as store]
   [nextjournal.clerk :as clerk]
   [mikrobloggeriet.pandoc :as pandoc]))

(->> (store/all-cohort+docs)
     (map second))

(count
 (->> (store/all-cohort+docs)
      (map (partial apply store/load-meta))
      (map :git.user/email)
      (into #{})
      ))

(clerk/caption "Hvem skriver mange dokumenter på mikrobloggeriet?"
               (clerk/table
                (->> (store/all-cohort+docs)
                     (map (partial apply store/load-meta))
                     (group-by :git.user/email)
                     (map (fn [[email docs]]
                            {:email email :docs (count docs)}))
                     (sort-by :docs)
                     reverse)))

store/doc-md-path

(defn words
  "A crude word count!"
  [s]
  (count (str/split s #"\w+")))

(defn words2 [markdown-str]
  (-> markdown-str
      pandoc/from-markdown
      pandoc/to-plain
      words))

(comment
  (words "this is fine

another piece of owrds")
  ;; => 7
  )

(clerk/caption "Hvem skriver mange ord på mikrobloggeriet?"
               (clerk/table
                (->> (store/all-cohort+docs)
                     (map (fn [[cohort doc]]
                            (-> (store/load-meta cohort doc)
                                (assoc :doc/words (words (slurp (store/doc-md-path cohort doc)))))))
                     (group-by :git.user/email)
                     (map (fn [[email docs]]
                            {:email email :total-words (reduce + (map :doc/words docs))}))
                     (sort-by :total-words)
                     (reverse))))

(defn words2
  "Count words by _first_ converting to plain text, then count words."
  [markdown-str]
  (-> markdown-str
      pandoc/from-markdown
      pandoc/to-plain
      words))

(clerk/caption "Hvem skriver mange ord på mikrobloggeriet? (forsøk 2)"
               (clerk/table
                (->> (store/all-cohort+docs)
                     (map (fn [[cohort doc]]
                            (-> (store/load-meta cohort doc)
                                (assoc :doc/words1 (words (slurp (store/doc-md-path cohort doc))))
                                (assoc :doc/words2 (words2 (slurp (store/doc-md-path cohort doc)))))))
                     (group-by :git.user/email)
                     (map (fn [[email docs]]
                            (let [person-summary
                                  {:email email
                                   :total1 (reduce + (map :doc/words1 docs))
                                   :total2 (reduce + (map :doc/words2 docs))
                                   :doc-count (count docs)}]
                              (assoc person-summary :avg-words-per-doc
                                     (format "%.2f"
                                             (double (/ (:total2 person-summary)
                                                        (:doc-count person-summary))))))))
                     (sort-by :total2)
                     (reverse))))


^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
