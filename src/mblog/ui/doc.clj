(ns mblog.ui.doc
  (:require
   [datomic.api :as d]
   [mikrobloggeriet.doc :as doc]))

;; (def css-vars ":root {\n   --first100: rgb(145,193,233);\n   --first80: rgba(145,193,233, 0.8);\n   --first50: rgba(145,193,233, 0.5);\n   --first20: rgba(145,193,233, 0.2);\n   --first10: rgba(145,193,233, 0.1);\n   --second100: rgb(26,44,91);\n   --second80: rgba(26,44,91, 0.8);\n   --second50: rgba(26,44,91, 0.5);\n   --second20: rgba(26,44,91, 0.2);\n   --second10: rgba(26,44,91, 0.1);\n}")


(def css-vars ":root {

   --first80: rgba(145,193,233, 0.8);
   --first50: rgba(145,193,233, 0.5);
   --first20: rgba(145,193,233, 0.2);
   --first10: rgba(145,193,233, 0.1);
   --second100: rgb(26,44,91);
   --second80: rgba(26,44,91, 0.8);
   --second50: rgba(26,44,91, 0.5);
   --second20: rgba(26,44,91, 0.2);
   --second10: rgba(26,44,91, 0.1);
}")


(def bringebær "rgb(198, 12, 90)")

(def grønn "#a0fb6c")

(def more-css-vars
  (str ":root {"
       "--first100: " bringebær ";"
       "--second100: " grønn ";"
       "}"))


(defn doc->hiccup [doc]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:link {:rel "stylesheet" :href "/css/styles/doc.css"}]
    [:style css-vars]
    [:style more-css-vars]
    [:style "p {max-width: 60rem};"]]
   [:body
    [:div
     (-> doc doc/hiccup)]
    [:footer
     [:a {:href "/"}
      [:p "Mikrobloggeriet"]]]]])

(defn req->doc [req]
  (d/entity (:mikrobloggeriet.system/datomic req)
            [:doc/slug (-> req :reitit.core/match :path-params :slug)]))

(comment
  (require 'mikrobloggeriet.state)
  (def olorm-1 (d/entity mikrobloggeriet.state/datomic
                         [:doc/slug "olorm-1"])))
