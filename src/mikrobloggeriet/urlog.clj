(ns mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [clojure.edn :as edn]
   [babashka.fs :as fs]
   [mikrobloggeriet.urlog :as urlog]))

;; TO-DO
;; rydde i css-filen, spesielt rundt "wall" klassen
;; rydde opp i page, spesielt rundt random og reverse logikken
;; splitte i flere navnerom, eks view, store

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

(defn door+url->html [door url]
  [:div {:class :component}
   [:pre {:aria-hidden :true} "_|____|____|____|"]
   [:a {:href url :class :door :aria-label "door ascii art"}
    [:pre {:class :closed}
     (:closed door)]
    [:pre {:class :open}
     (:open door)]]])

(defn wall->html [wall]
  [:pre {:aria-hidden :true} wall])

(defn logo->html [logo]
  [:pre {:class :logo :aria-label "URLOG logo ascii art"} logo])

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

(defn select-door [url doors]
  (let [seed (java.util.Random. (.hashCode url))]
    (when (seq doors)
      (let [index (.nextInt seed (count doors))]
        (nth doors index)))))

(def urlogfile-path "text/urlog/urls.edn")
(def assets-dir "src/mikrobloggeriet/urlog_assets")

(comment
  (logo->html (:logo (load-ascii-assets assets-dir)))
  (wall->html (:wall (load-ascii-assets assets-dir)))
  (let [door (first (:doors (load-ascii-assets assets-dir)))]
    (door+url->html door "example.com"))
  (rand-nth (:doors (load-ascii-assets assets-dir)))

  ;; Samme resultat hver gang:
  (let [doors (:doors (load-ascii-assets assets-dir))]
    (select-door "example.com" doors))
  )

(defn page [_req]
  (let [urlog-data (edn/read-string (slurp urlogfile-path))
        assets (load-ascii-assets assets-dir)]
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
         (let [door (rand-nth (:doors assets))
               url (:urlog/url doc)]
           [:div {:class :wall :role :none}
            (wall->html (:wall assets))
            (door+url->html door url)
            (wall->html (:wall assets))]))]])))
