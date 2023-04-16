;; OLORM DEVUI

(ns olorm.devui
  (:require
   [nextjournal.clerk :as clerk]
   [olorm.lib :as olorm]
   [iki.api :as iki]))

;; ## Looking at things, eg http requests

(defonce xx (atom nil))

@xx

(clerk/table
 (for [[h v] (:headers @xx)]
   {:header h :value v}))

;; ## View an olorm

(def markdown->html
  (iki/cache-fn-by iki/markdown->html identity))

(defn olorm->html [olorm]
  (when (olorm/exists? olorm)
    (markdown->html (slurp (olorm/index-md-path olorm) ))))

(clerk/html (olorm->html {:slug "olorm-1" :repo-path ".."}))

(clerk/html (olorm->html {:slug "olorm-2" :repo-path ".."}))

(olorm/olorms {:repo-path ".."})

^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
