(ns mblog.ui.doc
  (:require
   [datomic.api :as d]
   [mikrobloggeriet.doc :as doc]))

(def samvirk
  {:bg-color "#91c1e9"
   :text-color "#1a2c5b"
   :font "font16.css"
   :root ":root {\n   --first100: rgb(145,193,233);\n   --first80: rgba(145,193,233, 0.8);\n   --first50: rgba(145,193,233, 0.5);\n   --first20: rgba(145,193,233, 0.2);\n   --first10: rgba(145,193,233, 0.1);\n   --second100: rgb(26,44,91);\n   --second80: rgba(26,44,91, 0.8);\n   --second50: rgba(26,44,91, 0.5);\n   --second20: rgba(26,44,91, 0.2);\n   --second10: rgba(26,44,91, 0.1);\n}"})

(defn doc->hiccup [doc]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:link {:rel "stylesheet" :href "/css/styles/indigo.css"}]
    [:style (:root samvirk)]
    [:style ""]]
   [:body
    [:container {:style "p {max-width: 60rem};"}
     [:header
      [:a {:href "/"}
       [:p "Mikrobloggeriet"]]]
     [:div
      (-> doc doc/hiccup)]]]])

(defn req->doc [req]
  (d/entity (:mikrobloggeriet.system/datomic req)
            [:doc/slug (-> req :reitit.core/match :path-params :slug)]))

(comment
  (require 'mikrobloggeriet.state)
  (def olorm-1 (d/entity mikrobloggeriet.state/datomic
                         [:doc/slug "olorm-1"])))
