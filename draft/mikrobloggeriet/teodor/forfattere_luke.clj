(ns mikrobloggeriet.teodor.forfattere-luke
  (:require
   [mikrobloggeriet.store :as store]))

(let [cohort store/luke]
  (->> (store/docs cohort)
       (map (fn [doc]
              (store/load-meta cohort doc)))
       (map :git.user/email)))
