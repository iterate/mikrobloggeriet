(ns mikrobloggeriet.ui.shared
  (:require
   [hiccup.page]
   [ring.middleware.cookies :as cookies]))

(defn feeling-lucky []
  [:a {:href "/random-doc" :class :feeling-lucky} "🎲"])

(defn html-header
  "Shared HTML, including CSS.
  Handles CSS theming system with cookies."
  [req]
  (list
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
   (hiccup.page/include-css "/vanilla.css")
   (hiccup.page/include-css "/mikrobloggeriet.css")
   (hiccup.page/include-css "/pygment.css")
   (let [theme (get-in (cookies/cookies-request req)
                       [:cookies "theme" :value]
                       "christmas")]
     (hiccup.page/include-css (str "/theme/" theme ".css")))
   (let [theme (get-in (cookies/cookies-request req) [:cookies "theme" :value])
         number (rand-nth (range 4))]
     (when (= theme "iterate")
       [:style {:type "text/css"}
        (str ":root{ --text-color: var(--iterate-base0" number ")}")]))))

(defn navbar [& children]
  [:p (feeling-lucky) " — " [:a {:href "/"} "mikrobloggeriet"]
   children])
