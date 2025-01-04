(ns mikrobloggeriet.ui.doc
  (:refer-clojure :exclude [next])
  (:require
   [hiccup.page]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn previous-next-navigator [previous doc next]
  (list (when previous
          [:span [:a {:href (doc/href previous)} (:doc/slug previous)] " · "])
        [:span (:doc/slug doc)]
        (when next
          [:span  " · " [:a {:href (doc/href next)} (:doc/slug next)]])))

(defn page [doc req & {:keys [previous next]}]
  (let [cohort (:doc/cohort doc)]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body
     (hiccup.page/html5 {}
       [:head
        [:title (doc/title doc)]
        (ui.shared/html-header req)]
       [:body
        (ui.shared/navbar " "
                          [:a {:href (cohort/href cohort)}
                           (:cohort/slug cohort)]
                          " - "
                          (previous-next-navigator previous doc next))
        (doc/html doc)])}))

