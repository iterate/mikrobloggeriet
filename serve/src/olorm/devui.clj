(ns olorm.devui
  (:require
   [nextjournal.clerk :as clerk]
   [olorm.lib :as olorm]
   [babashka.fs :as fs]))

(defonce xx (atom nil))

@xx

(clerk/table
 (for [[h v] (:headers @xx)]
   {:header h :value v}))

123123123

(let [repo-path  ".."]
  (->> (fs/list-dir (fs/canonicalize (str repo-path "/p")))
       (map fs/file-name)
       (map str)
       (map olorm/slug->olorm)
       (filter :olorm)))

#_
(let [repo-path  ".."]
  (fs/canonicalize
   (str repo-path "/p"))
  #_#_#_
  (map str)
  (map olorm/slug->olorm)
  (filter :olorm))

(defn olorm->html [& _args])

(olorm->html {:slug "olorm-1" :repo-path ".."})

;; ... or do I want hiccup?
;; not sure.
;; how do I want this to integrate with the rest of the system?
;; what am I doing (really) in OGGPOW?
