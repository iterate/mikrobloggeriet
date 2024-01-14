(ns mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [clojure.edn :as edn]
   [babashka.fs :as fs]
   [mikrobloggeriet.urlog :as urlog]))

;; TO-DO
;; rename komponenter slik at de gir mening
;; rydde i css-filen, spesielt rundt "wall" klassen
;; rydde opp i page, spesielt rundt random og reverse logikken
;; splitte i flere navnerom, eks view, store

(defn door-paths [doors-dir]
  (->> (fs/list-dir doors-dir)
       (map str)
       (sort)))

(defn load-door [door-path]
  {:closed (slurp (str door-path "/closed.txt"))
   :open (slurp (str door-path "/open.txt"))})

(defn load-ascii-assets [assets-dir]
  {:logo (slurp (str assets-dir "/logo.txt"))
   :wall (slurp (str assets-dir "/wall.txt"))
   :doors (doall (map load-door (door-paths (str assets-dir "/doors/"))))})

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

(defn door+url->html [door url]
  [:div {:class :component}
   [:pre "_|____|____|____|"]
   [:a {:href url :class :door}
    [:pre {:class :closed}
     (:closed door)]
    [:pre {:class :open}
     (:open door)]]])

(defn wall->html [wall]
  [:pre wall])

(defn logo->html [logo]
  [:pre {:class :logo} logo])

(comment
  (logo->html (:logo load-ascii-assets))
  (wall->html (:wall load-ascii-assets))
  (let [door (first (:doors load-ascii-assets))]
    (door+url->html door ""))
  (rand-nth (:doors load-ascii-assets)))

(def urlogfile-path "text/urlog/urls.edn")
(def assets-dir "src/mikrobloggeriet/urlog_assets")

(defn page [_req]
  (let [urlog-data (edn/read-string (slurp urlogfile-path))
        assets (load-ascii-assets assets-dir)
        wall-html (wall->html (:wall assets))]
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
       (logo->html (:logo assets))
       [:p {:class :intro}
        "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]]
      [:div {:class :all-doors}
       (for [doc (reverse (:urlog/docs urlog-data))]
         (let [url (:urlog/url doc)
               door (rand-nth (:doors assets))]
           [:div {:class :wall}
            wall-html
            (door+url->html door url)
            wall-html]))]])))