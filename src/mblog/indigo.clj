(ns mblog.indigo
  (:require [clojure.walk :refer [postwalk]]
            [mikrobloggeriet.doc :as doc]
            [replicant.string]))

(defn hiccup-optmap [form]
  (if (map? (second form))
    (second form)
    {}))

(defn hiccup-children [form]
  (if (map? (second form))
    (drop 2 form)
    (drop 1 form)))

(defn lazyload-images [hiccup]
  (postwalk
   (fn [form]
     (cond
       (and (vector? form)
            (= (first form)
               :img))
       (into [:img (assoc (hiccup-optmap form)
                          :loading "lazy")]
             (hiccup-children form))
       :else form))
   hiccup))

(defn innhold->hiccup [docs]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:link {:rel "stylesheet" :href "indigo.css"}]
    [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
    [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
    [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&display=swap"}]]
   [:body
    [:header
     [:a {:href "/"} [:h1 "Mikrobloggeriet"]]]
    [:container
     [:section
      [:nav
       (for [doc docs]
         [:a.navList {:href (str "#" (:doc/slug doc))}
          [:p.navTitle (doc/cleaned-title doc)]
          [:p.navDate "/"] [:p.navDate (:doc/created doc)]
          [:p.navDate "/"] [:p.navDate (-> doc :doc/cohort :cohort/slug)]])]]

     [:section
      [:div
       (for [doc docs]
         [:div
          [:a {:name (:doc/slug doc)}]
          [:div (-> doc doc/hiccup lazyload-images)]])]]]
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
