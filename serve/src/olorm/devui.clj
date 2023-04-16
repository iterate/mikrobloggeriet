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
