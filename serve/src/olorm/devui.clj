;; # OLORM DEVELOPMENT INTERFACE

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

(def olorms (olorm/olorms {:repo-path ".."}))

(clerk/html (olorm->html (first olorms)))
(clerk/html (olorm->html (second olorms)))

(olorm/olorms {:repo-path ".."})
(clerk/table olorms)

^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
