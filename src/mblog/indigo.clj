(ns mblog.indigo
  (:require [mikrobloggeriet.doc :as doc]
            [replicant.string]))

(defn innhold->hiccup [docs]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:link {:rel "stylesheet" :href "indigo.css"}]]
   [:body
    [:header
     [:h1 "Mikrobloggeriet"]]
    [:container
     [:section
      [:ul
       (for [doc docs]
         [:li (list (doc/title doc)
                    " [" (:doc/created doc) "]")])]]

     [:section
      [:div
       (for [d (take 10 docs)]
         [:div {:innerHTML
                (doc/html d)}])]]]
    [:footer [:h1 "filter, Filter, FILTER!"]]]])

(defn innhold [req]
  (doc/latest (:mikrobloggeriet.system/datomic req)))

(def last-req (atom nil))

(defn handler [req]
  (reset! last-req req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   (str
    "<!DOCTYPE html>"
    (replicant.string/render
     (innhold->hiccup (innhold req))))})

(comment
  (require 'clojure.repl.deps)
  (clojure.repl.deps/sync-deps)
  (set! *print-namespace-maps* false)

  (contains? @last-req :mikrobloggeriet.system/datomic)
  ;; => true

  (def dev-db (:mikrobloggeriet.system/datomic @last-req))

  (->> (doc/latest dev-db)
       (take 5)
       (map doc/title)))
