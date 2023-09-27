(ns mikrobloggeriet.teodor.alle
  (:require
   [mikrobloggeriet.store :as store]))

(count (concat (store/docs store/olorm)
               (store/docs store/jals)
               (store/docs store/oj)))
;; => 52

(defn all+meta []
  (for [cohort (vals store/cohorts)
        doc (store/docs cohort)]
    (store/load-meta cohort doc)))

(->> (all+meta)
     (map :git.user/email)
     frequencies)
;; => {"julian.hallen@iterate.com" 1,
;;     "lars.barlindhaug@iterate.no" 10,
;;     "jomarn@me.com" 1,
;;     "sindre@iterate.no" 2,
;;     "adrian.tofting@iterate.no" 2,
;;     "oddmunds@iterate.no" 8,
;;     "richard.tingstad@iterate.no" 16,
;;     "git@teod.eu" 7,
;;     "olav.moseng@iterate.no" 1,
;;     "larshbj@gmail.com" 3,
;;     "aaberg89@gmail.com" 2}
