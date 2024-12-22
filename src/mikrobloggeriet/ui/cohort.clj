(ns mikrobloggeriet.ui.cohort
  (:require
   [clojure.string :as str]
   [datomic.api :as d]
   [hiccup.page :as page]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.ui.shared :as shared]))

(defn doc-table [db cohort req]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body
   (page/html5
       (into [:head] (shared/html-header req))
     [:body
      [:p (shared/feeling-lucky)
       " — "
       [:a {:href "/"} "mikrobloggeriet"]]
      [:h1 (str "Alle " (str/upper-case (:cohort/slug cohort)) "-er")]
      [:table
       [:thead
        [:td (:cohort/slug cohort)]
        [:td "tittel"]
        [:td "forfatter"]
        [:td "publisert"]]
       [:tbody
        (for [doc (->> (:doc/_cohort cohort)
                       (sort-by doc/number))]
          [:tr
           [:td [:a {:href (doc/href doc)} (:doc/slug doc)]]
           [:td (doc/title doc)]
           [:td (doc/author-first-name db doc)]
           [:td (:doc/created doc)]])]]])})

(comment
  (def db mikrobloggeriet.state/datomic)
  (def olorm (d/entity db [:cohort/id :cohort/olorm]))
  (keys olorm)
  ;; => (:cohort/id
  ;;     :cohort/root
  ;;     :cohort/slug
  ;;     :cohort/type
  ;;     :cohort/name
  ;;     :cohort/description)


  (->> (:doc/_cohort olorm)
       first
       keys
       )
  ;; => (:doc/slug :doc/created :doc/uuid :git.user/email :doc/markdown :doc/cohort)

  )
