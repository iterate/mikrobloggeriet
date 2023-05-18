;; # OLORM DEVELOPMENT INTERFACE

(ns olorm.devui
  (:require
   [nextjournal.clerk :as clerk]
   [mikrobloggeriet.olorm :as olorm]
   [iki.api :as iki]))

;; ## Look at things, like http requests

(defonce last-tapped (atom nil))

(defn store-tapped [val]
  (reset! last-tapped val))

(add-tap #'store-tapped)

@last-tapped

(when-let [headers (:headers @last-tapped)]
          (clerk/table
           (for [[h v] headers]
             {:header h :value v})))

;; ## View an olorm

(def markdown->html
  (iki/cache-fn-by iki/markdown->html identity))

(defn olorm->html [olorm]
  (when (olorm/exists? olorm)
    (markdown->html (slurp (olorm/index-md-path olorm) ))))

(def olorms (olorm/docs {:repo-path ".."}))

(clerk/html (olorm->html (first olorms)))
(clerk/html (olorm->html (second olorms)))

(olorm/olorms {:repo-path ".."})
(clerk/table olorms)

^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
