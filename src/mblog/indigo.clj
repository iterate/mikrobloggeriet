(ns mblog.indigo
  (:require
   [clojure.string :as str]
   [clojure.walk :refer [postwalk]]
   [hiccup.page]
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

(defn parse-css [re css-str]
  (when-let [s (re-find re css-str)]
    (str/replace (last s) "'" "")))

(def css-re
  {:bg #"--background01:\s*(.*?);"
   :text #"--white100:\s*(.*?);"
   :font #"font-family:\s*(.*?);"})

(defn css->background-color [theme]
  (parse-css (:bg css-re) theme))

(defn css->text-color [theme]
  (parse-css (:text css-re) theme))

(defn css->font [style]
  (parse-css (:font css-re) style))


(def styles ["indigo.css" "indigo2.css"])
(def themes ["theme1.css" "theme2.css" "theme3.css" "theme4.css" "theme5.css" "theme6.css" "theme7.css" "theme8.css" "theme9.css" "theme10.css"])

(defn find-title-ish
  "Finds the title if present, otherwise falls back to slug"
  [doc]
  (or (doc/cleaned-title doc)
      (:doc/slug doc)))

(defn innhold->hiccup [docs]
  (let [rand-style (rand-nth styles)
        rand-theme (rand-nth themes)
        bg-color (css->background-color (slurp rand-theme))
        text-color (css->text-color (slurp rand-theme))
        font (css->font (slurp rand-style))]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:link {:rel "stylesheet" :href rand-style}]
      [:link {:rel "stylesheet" :href rand-theme}]
      ;; Google fonts
      [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
      [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
      [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Instrument+Serif:ital@0;1&family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&family=Jersey+10&family=Monda:wght@400..700&family=PT+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Sono:wght@200..800&family=Space+Mono:ital,wght@0,400;0,700;1,400;1,700&family=Tiny5&display=swap"}]]
     [:body
      [:header
       [:a {:href "/"}
        [:h1 "Mikrobloggeriet"]
        [:p (str rand-style " / " rand-theme " / " bg-color " + " text-color " / " font)]]]
      [:container
       [:section.navigation
        [:nav
         (for [doc docs]
           [:a.navList {:href (str "#" (:doc/slug doc))}
            [:p.navTitle (find-title-ish doc)]
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
