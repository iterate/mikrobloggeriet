(ns mikrobloggeriet.ui.doc
  (:require
   [hiccup.page]
   [mikrobloggeriet.cache :as cache]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn page [doc req & {:keys [previous next]}]
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
          (:cohort/slug cohort)]
      " - "
      (when previous
        [:span [:a {:href (doc/href cohort previous)} (:doc/slug previous)]]) " · "] doc-html])}))
