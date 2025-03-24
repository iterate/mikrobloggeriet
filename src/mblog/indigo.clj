(ns mblog.indigo
  (:require
   [clojure.walk :refer [postwalk]]
   [hiccup.page]
   [mblog.samvirk :as samvirk]
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

(defn find-title-ish
  "Finds the title if present, otherwise falls back to slug"
  [doc]
  (or (doc/cleaned-title doc)
      (:doc/slug doc)))

(defn view-doc [doc]
  [:div.doc
   [:a {:name (:doc/slug doc)}]
   [:div
    (list
     ;; smash in the slug as title if no title is given on the doc.
     ;;
     ;; The astute reader will notice that this smells of implementation
     ;; details compared to the rest of this fine namespace. Perhaps we should
     ;; lower this logic down a notch. It may be better suited to the doc
     ;; namespace.
     ;;
     ;; For now, we keep the title smashing close to its use (presentation).
     (when-not (doc/cleaned-title doc)
       [:h1 (:doc/slug doc)])
     (-> doc doc/hiccup lazyload-images))]])

(comment
  (require '[datomic.api :as d])
  (def leik-3 (d/entity dev-db [:doc/slug "leik-3"]))
  (view-doc leik-3)

  (def leik-2 (d/entity dev-db [:doc/slug "leik-2"]))
  (view-doc leik-2))

(defn innhold->hiccup [docs]
  (let [samvirk (samvirk/load)]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:link {:rel "stylesheet" :href (samvirk/style-path (:style samvirk))}]
      [:link {:rel "stylesheet" :href (samvirk/theme-path (:theme samvirk))}]
      [:link {:rel "stylesheet" :href (samvirk/font-path (:font samvirk))}]
      ;; Google fonts
      [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
      [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
      [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Instrument+Serif:ital@0;1&family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&family=Jersey+10&family=Monda:wght@400..700&family=PT+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Sono:wght@200..800&family=Space+Mono:ital,wght@0,400;0,700;1,400;1,700&family=Tiny5&display=swap"}]]
     [:body
      [:header
       [:a {:href "/"}
        [:h1 "Mikrobloggeriet"]
        [:p (str (:style samvirk) " / " (:theme samvirk) " / " (:bg-color samvirk) " + " (:text-color samvirk) " / " (samvirk/css->font (samvirk/read-font (:font samvirk))))]]]
      [:container

       [:section.navigation
        [:nav
         (for [doc docs]
           [:a.navList {:href (str "#" (:doc/slug doc))}
            [:p.navTitle (find-title-ish doc)]
            [:p.navDate "/"] [:p.navDate (:doc/created doc)]
            [:p.navDate "/"] [:p.navDate (-> doc :doc/cohort :cohort/slug)]])]]

       [:section.content
        [:div (map view-doc docs)]]]
      [:footer [:h1 "filter, Filter, FILTER!"]]]]))

(defn req->innhold [req]
  (doc/latest (:mikrobloggeriet.system/datomic req)))

(def last-req (atom nil))

(defn handler [req]
  (reset! last-req req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   (hiccup.page/html5 {} (innhold->hiccup (req->innhold req)))})

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
