(ns mikrobloggeriet.ui.analytics
  (:require
   [hiccup.page]
   [mikrobloggeriet.analytics :as analytics]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn pageviews-table [pageviews-state]
  [:table
   [:thead [:th "URL"] [:th "Dato"] [:th "Sidevisninger"]]
   [:tbody
    (for [[uri dato pageviews] (analytics/uri+date+count-tuples pageviews-state)]
      [:tr [:td uri] [:td dato] [:td pageviews]])]])

(def pageviews-uri-whitelist
  #{"/urlog/"})

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
      (pageviews-table (select-keys (:mikrobloggeriet.system/pageviews req)
                                    pageviews-uri-whitelist))])})
