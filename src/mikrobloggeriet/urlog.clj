(ns mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [clojure.edn :as edn]
   [babashka.fs :as fs]
   [mikrobloggeriet.urlog :as urlog]))

;; TO-DO
;; skille wall-html og door-html som seperate komponenter
;; dra ut filinnlasting fra fra html komponentene
;; lage load for å laste inn alle ascii txt assets
;; splitte i flere navnerom, eks view, store

(def urlogfile-path "text/urlog/urls.edn")
(def doors-dir "src/mikrobloggeriet/urlog_assets/doors/")
(defn door-paths []
  (->> (fs/list-dir doors-dir)
       (map str)
       (sort)))

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

(defn logo []
  [:pre {:class :logo}
   (slurp "src/mikrobloggeriet/urlog_assets/logo.txt")])

(def load-ascii
  {:logo (slurp "src/mikrobloggeriet/urlog_assets/logo.txt")
   :wall (slurp "src/mikrobloggeriet/urlog_assets/wall.txt")
   :doors (map (fn [n] {:closed (slurp (str n "/closed.txt"))
                        :open (slurp (str n "/open.txt"))}) (door-paths))})

(defn door+url->html [closed open url]
  [:div {:class :wall}
   [:div {:class :component}
    [:pre "_|____|____|____|"]
    [:a {:href url :class :door}
     [:pre {:class :closed}
      closed]
     [:pre {:class :open}
      open]]]])

(defn wall->html [wall]
  [:div {:class :wall}
   [:pre wall]])

(defn logo->html [logo]
  [:pre {:class :logo}
   logo])

(comment
  (logo->html (:logo load-ascii))
  (wall->html (:wall load-ascii))
  (let [door (first (:doors load-ascii))]
    (door+url->html (:closed door) (:open door) ""))
  (rand-nth (:doors load-ascii)))

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