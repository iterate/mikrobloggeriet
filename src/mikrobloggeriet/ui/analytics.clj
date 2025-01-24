(ns mikrobloggeriet.ui.analytics
  (:require
   [clojure.string :as str]
   [hiccup.page]
   [mikrobloggeriet.analytics :as analytics]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.db]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn pageviews-table [pageviews-state]
  [:table
   [:thead [:th "URL"] [:th "Dato"] [:th "Sidevisninger"]]
   [:tbody
    (for [[uri dato pageviews] (analytics/uri+date+count-tuples pageviews-state)]
      [:tr [:td uri] [:td dato] [:td pageviews]])]])

(def pageviews-uri-whitelist
  #{"/urlog/"})

(def public-analytics-route-names
  (->> mikrobloggeriet.db/cohorts
       (mapcat (fn [[_ cohort]]
                 [(cohort/route-all cohort)
                  (cohort/route-doc cohort)]))
       set))

(defn page [req]
  (tap> req)
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
      (pageviews-table (->> (:mikrobloggeriet.system/pageviews req)
                            (filter (fn [[uri analytics-data]]
                                      (contains? public-analytics-route-names
                                                 (reitit.core/match-by-path (:reitit.core/router req)
                                                                            ))

                                      )))
                       (select-keys (:mikrobloggeriet.system/pageviews req)
                                    pageviews-uri-whitelist))])})

(comment
  ;; Analytics behavior
  ;;
  ;; Track visitors to /
  ;; Track visitors to cohorts
  ;; Track visitors to individual documents
  ;; Keep a "top viewed documents".



  ;; # Views per document table

  ;; # Views per document, latest week

  ;; # Table of all page views

  (def cohort-slugs
    (->> mikrobloggeriet.db/cohorts
         vals
         (map :cohort/slug)
         set))

  (defn doc-uri? [uri]
    (let [parts (str/split uri #"/")]
      (and (= 3 (count parts))

           )))

  (str/split "/olorm/olorm-1/" #"/")

  mikrobloggeriet.db/cohorts

  (set! *print-namespace-maps* false)

  (def cohort-slugs ))
