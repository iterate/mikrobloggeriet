(ns mikrobloggeriet.ui.doc
  (:require
   [hiccup.page]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn page [doc req]
  (let [cohort
        (:doc/cohort doc)

        {:keys [title doc-html]}
        (cache/markdown->html+info (:doc/markdown doc))]
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body
     (hiccup.page/html5
         (into [:head] (concat [[:title title]]
                               (ui.shared/html-header req)))
       [:body
        [:p (ui.shared/feeling-lucky)
         " — "
         [:a {:href "/"} "mikrobloggeriet"]
         " "
         [:a {:href (cohort/href cohort)}
          (:cohort/slug cohort)]]
        doc-html])}))
