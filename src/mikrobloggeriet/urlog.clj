(ns mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [clojure.edn :as edn]
   [babashka.fs :as fs]
   [mikrobloggeriet.urlog :as urlog]))

(def urlogfile-path "text/urlog/urls.edn")
(def doors-dir "src/mikrobloggeriet/urlog_assets/doors/")
(defn door-paths []
  (->> (fs/list-dir doors-dir)
       (map str)
       (sort)))

(defn index-section [_req slug]
  [:section
   [:h2 "URLOG"]
   [:p "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]
   [:p [:a {:href slug} "Gå inn i huset –> 🏨"]]])

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

(defn logo []
  [:pre {:class :logo}
   (slurp "src/mikrobloggeriet/urlog_assets/logo.txt")])

(defn door-path+url->html [door-path url]
  [:div {:class :wall}
   [:pre (slurp "src/mikrobloggeriet/urlog_assets/wall.txt")]
   [:div {:class :component}
    [:pre "_|____|____|____|"]
    [:a {:href url :class :door}
     [:pre {:class :closed}
      (slurp (str door-path "/closed.txt"))]
     [:pre {:class :open}
      (slurp (str door-path "/open.txt"))]]]
   [:pre (slurp "src/mikrobloggeriet/urlog_assets/wall.txt")]])

(defn page [_req]
  (page/html5
   [:head
    (page/include-css "/mikrobloggeriet.css")
    (page/include-css "/urlog.css")]
   [:body
    [:p
     (feeling-lucky "🎲")
     " — "
     [:a {:href "/"} "mikrobloggeriet"]]
    [:header
     (logo)
     [:p {:class "intro"}
      "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]]
    [:div {:class "all-doors"}
     (let [urlog-data (edn/read-string (slurp urlogfile-path))]
       (for [doc (reverse (:urlog/docs urlog-data))]
         (let [door-path (rand-nth (door-paths))
               url (:urlog/url doc)]
           (door-path+url->html door-path url))))]]))