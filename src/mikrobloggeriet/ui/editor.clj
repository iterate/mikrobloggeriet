(ns mikrobloggeriet.ui.editor
  "An experiment about whether we can provide a nice writing experience directly
  on Mikrobloggeriet."
  (:require
   [hiccup.page]
   [mikrobloggeriet.ui.shared :as ui.shared]))

(defn page [req]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body
   (hiccup.page/html5 {}
     [:head
      [:title "Editor"]
      (ui.shared/html-header req)]
     [:body
      (ui.shared/navbar)
      [:h1 "Skriveflate"]
      [:p "Vi eksperimenterer med muligheten til å skrive direkte på nett."]
      [:textarea
       {:style {:resize "vertical"
                :width "100%"
                :min-height "12rem"}}]])})
