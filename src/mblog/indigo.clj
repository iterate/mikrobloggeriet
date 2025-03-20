(ns mblog.indigo
  (:require
   [clojure.walk :refer [postwalk]]
   [hiccup.page]
   [mikrobloggeriet.doc :as doc]
   [replicant.string]
   [clojure.string :as str]))

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

(defn parse-css [css re]
  (as-> css c
    (slurp c)
    (re-find re c)
    (last c)
    (str/replace c #"'" "")))

(defn css->background-color [theme]
  (parse-css theme #"--background01:\s*(.*?);"))

(defn css->text-color [theme]
  (parse-css theme #"--white100:\s*(.*?);"))

(defn css->font [style]
  (parse-css style #"font-family:\s*(.*?);"))

(def styles ["indigo.css" "indigo2.css"])
(def themes ["theme1.css" "theme2.css"])

(defn innhold->hiccup [docs]
  (let [rand-style (rand-nth styles)
        rand-theme (rand-nth themes)
        bg-color (css->background-color rand-theme)
        text-color (css->text-color rand-theme)
        font (css->font rand-style)]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:link {:rel "stylesheet" :href rand-style}]
      [:link {:rel "stylesheet" :href rand-theme}]
   ;; Google fonts
      [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
      [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
      [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&display=swap"}]]
     [:body
      [:header
       [:a {:href "/"} [:h1 "Mikrobloggeriet"]
        [:p (str rand-style " / " rand-theme " / " bg-color " + " text-color " / " font)]]]
      [:container
       [:section.navigation
        [:nav
         (for [doc docs]
           [:a.navList {:href (str "#" (:doc/slug doc))}
            [:p.navTitle (doc/cleaned-title doc)]
            [:p.navDate "/"] [:p.navDate (:doc/created doc)]
            [:p.navDate "/"] [:p.navDate (-> doc :doc/cohort :cohort/slug)]])]]

       [:section.content
        [:div
         (for [doc docs]
           [:div
            [:a {:name (:doc/slug doc)}]
            [:div (-> doc doc/hiccup lazyload-images)]])]]]
      [:footer [:h1 "filter, Filter, FILTER!"]]]]))

(defn innhold [req]
  (doc/latest (:mikrobloggeriet.system/datomic req)))

(def last-req (atom nil))

(defn handler [req]
  (reset! last-req req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   (hiccup.page/html5 {} (innhold->hiccup (innhold req)))
   #_(str
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
