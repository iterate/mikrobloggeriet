(ns mblog.indigo
  (:require
   [clojure.walk :refer [postwalk]]
   [hiccup.page]
   [mblog.samvirk :as samvirk]
   [mikrobloggeriet.cohort :as cohort]
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
     ;; Pretend the slug is the title when the doc doesn't have a "real" title.
     (when-not (doc/cleaned-title doc)
       [:h1 (:doc/slug doc)])
     (-> doc doc/hiccup lazyload-images))]])

(defn innhold->hiccup [{:keys [docs cohorts]}]
  (let [samvirk (samvirk/load)]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:link {:rel "stylesheet" :href "css/styles/indigo.css"}]
      [:link {:rel "stylesheet" :href (samvirk/font-path (:font samvirk))}]
      ;; Google fonts
      [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
      [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin ""}]
      [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=Albert+Sans:ital,wght@0,100..900;1,100..900&family=Cormorant:ital,wght@0,300..700;1,300..700&family=DM+Mono:ital,wght@0,300;0,400;0,500;1,300;1,400;1,500&family=DM+Sans:ital,opsz,wght@0,9..40,100..1000;1,9..40,100..1000&family=Epilogue:ital,wght@0,100..900;1,100..900&family=Fira+Mono:wght@400;500;700&family=IBM+Plex+Mono:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;1,100;1,200;1,300;1,400;1,500;1,600;1,700&family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Instrument+Sans:ital,wght@0,400..700;1,400..700&family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&family=Manrope:wght@200..800&family=Old+Standard+TT:ital,wght@0,400;0,700;1,400&family=Onest:wght@100..900&family=PT+Sans:ital,wght@0,400;0,700;1,400;1,700&family=PT+Serif:ital,wght@0,400;0,700;1,400;1,700&family=Plus+Jakarta+Sans:ital,wght@0,200..800;1,200..800&family=Rethink+Sans:ital,wght@0,400..800;1,400..800&family=Spectral:ital,wght@0,200;0,300;0,400;0,500;0,600;0,700;0,800;1,200;1,300;1,400;1,500;1,600;1,700;1,800&family=Spline+Sans+Mono:ital,wght@0,300..700;1,300..700&family=Taviraj:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap"}]
      [:style (:root samvirk)]]
     [:body
      [:header
       [:a {:href "/"}
        "Mikrobloggeriet"]]
      [:container
       [:section.filters
        [:a.navList {:href "/"} [:p.navTitle "Alle"]]
        (->> cohorts
             (map (fn [cohort]
                    [:a.navList {:href (str "/?cohort=" (:cohort/slug cohort))} [:p.navTitle (:cohort/name cohort)]])))]
       [:section.navigation
        [:nav
         (for [doc docs]
           [:a.navList {:href (str "#" (:doc/slug doc))}
            [:p.navTitle (find-title-ish doc)]
            [:p.navDate "/"] [:p.navDate (doc/created-date doc)]
            [:p.navDate "/"] [:p.navDate (-> doc :doc/cohort :cohort/slug)]])]]

       [:section.content
        [:div (map view-doc docs)]]]
      [:footer [:p (str (:bg-color samvirk) " □" " + " (:text-color samvirk) " ■" " / " (samvirk/css->font (samvirk/read-font (:font samvirk))))]]]]))

(def last-req (atom nil))

(comment
  (-> @last-req
      (dissoc :reitit.core/match :mikrobloggeriet.system/pageviews)))

(defn req->innhold [req]
  (reset! last-req req)
  (let [db (:mikrobloggeriet.system/datomic req)]
    {:docs (let [cohort-id (get-in req [:query-params "cohort"])]
             (cond->> (doc/latest db)
               cohort-id (filter #(= cohort-id (-> % :doc/cohort :cohort/slug)))))
     :cohorts (cohort/all db)}))

(comment
  (require 'mikrobloggeriet.state)
  (def dev-db mikrobloggeriet.state/datomic)
  (def docs (doc/latest dev-db))
  (into {} (first docs))
  (def cohort (:doc/cohort (first docs)))
  (:cohort/name cohort))
