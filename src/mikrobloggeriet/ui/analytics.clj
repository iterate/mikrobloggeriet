(ns mikrobloggeriet.ui.analytics
  (:require
   [hiccup.page]
   [mikrobloggeriet.analytics :as analytics]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn page [req]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body
   (hiccup.page/html5 {}
     [:head
      [:title "Analyse"]
      (ui.shared/html-header req)]
     [:body
      (ui.shared/navbar)
      [:h1 "Analyse"]
      [:h2 "URLOG"]
      [:table
       [:thead [:th "URL"] [:th "Dato"] [:th "Sidevisninger"]]
       [:tbody
        (for [[uri dato sidevisniger] (analytics/uri+date+count-tuples
                                       (:mikrobloggeriet.system/pageviews req))]
          [:tr [:td uri] [:td dato] [:td sidevisniger]])
        #_
        (for [[dato sidevisniger] (->> (get (:mikrobloggeriet.system/pageviews req) "/urlog/")
                                       (sort-by first))]
          [:tr [:td "/urlog/"] [:td dato] [:td sidevisniger]])]]])})
