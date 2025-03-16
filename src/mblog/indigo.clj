(ns mblog.indigo
  (:require [mikrobloggeriet.doc :as doc]
            [replicant.string]))

(defn innhold->hiccup [docs]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]]
   [:body {:style
           {:display "flex"
            :height "100%"
            :margin 0
            :width "auto"}}
    [:div {:style
           {:display "flex"
            :flex-grow 1
            :height "100vh"
            :overflow-x "hidden"
            :overflow-y "auto"}}
     ;; venstre
     [:div {:style
            {:display "flex"
             :flex-direction "column"
             :max-width "40vw"
             :overflow "auto"}}
      (interpose " " (repeat 50 "venstre"))]]
    [:div {:style
           {:display "flex"
            :flex-grow 1
            :height "100vh"
            :overflow-x "hidden"
            :overflow-y "auto"}}
     [:div {:style
            {:display "flex"
             :flex-direction "column"
             :max-width "60vw"
             :overflow "auto"}}
      "høyre"
      (for [d (take 10 docs)]
        [:div {:innerHTML
               (doc/html d)}])]]]])

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
       (map doc/title))

  )
