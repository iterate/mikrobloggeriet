;; # OLORM DEVELOPMENT INTERFACE

(ns olorm.devui
  (:require
   [nextjournal.clerk :as clerk]
   [olorm.lib :as olorm]
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

(def olorms (olorm/olorms {:repo-path ".."}))

(clerk/html (olorm->html (first olorms)))
(clerk/html (olorm->html (second olorms)))

(olorm/olorms {:repo-path ".."})
(clerk/table olorms)

^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])

;; approach: stick to map from id to item.

(defn add-item-1 [store item]
  (let [last-index (last (sort (keys store)))
        next-index (if last-index (inc last-index) 0)]
    (assoc store next-index item)))

(let [store {}
      store (add-item-1 store {:text "thesatih"})
      store (add-item-1 store {:text "thiast"})
      store (add-item-1 store {:text "trolo"})]
  store)
;; => {0 {:text "thesatih"}, 1 {:text "thiast"}, 2 {:text "trolo"}}

;; approach: store the last item explicitly

(defn add-item-2 [store item]
  ;; store looks like this: {:last-index 1 :items {0 "item" 1 "second item}}
  (let [last-index (:last-index store)
        next-index (inc last-index)]
    {:last-index next-index
     :items (assoc (:items store) next-index item)}))

(let [store {:last-index 0 :items {}}
      store (add-item-2 store {:text "thesatih"})
      store (add-item-2 store {:text "thiast"})
      store (add-item-2 store {:text "trolo"})]
  store)
;; => {:last-index 3, :items {1 {:text "thesatih"}, 2 {:text "thiast"}, 3 {:text "trolo"}}}
