(ns mikrobloggeriet.doc-template
  (:require
   [clojure.string :as str]
   [hiccup2.core :as hiccup]))

;; Document templates for content on Mikrobloggeriet

(defn md-quality-nudge [{:keys [title]}]
  (let [paragraphs (fn [& args] (str/join "\n\n" args))
        htmlcomment (fn [& args] (str "<!-- " (apply str args) " -->"))]
    (assert title)
    (paragraphs
     (str "# " title)
     (htmlcomment "1. Hva gjør du akkurat nå?")
     (htmlcomment "2. Finner du kvalitet i det?")
     (htmlcomment "3. Hvorfor / hvorfor ikke?")
     (htmlcomment "4. Call to action---hva ønsker du kommentarer på fra de som leser?"))))

(comment
  (md-quality-nudge {:title "OLORM-44"})

  )

(defn url-new-tab-big-door [{:keys [title]}]
  (let [paragraphs (fn [& args] (str/join "\n\n" args))
        htmlcomment (fn [& args] (str "<!-- " (apply str args) " -->"))]
    (assert title)
    (paragraphs
     (str "# " title)
     (hiccup/html [:a {:href "URL_HER"
                       :target "_blank"
                       :style "font-size: 10vw; text-align:center;"}
                   [:div "🚪"]]))))

(comment
  (url-new-tab-big-door {:title "URLOG-88"})

  )
