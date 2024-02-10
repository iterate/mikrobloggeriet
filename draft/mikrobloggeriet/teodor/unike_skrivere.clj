(ns mikrobloggeriet.teodor.unike-skrivere
  (:require
   [mikrobloggeriet.store :as store]))

(->> (store/all-cohort+docs)
     (map second))

(count
 (->> (store/all-cohort+docs)
      (map (partial apply store/load-meta))
      (map :git.user/email)
      (into #{})
      ))
