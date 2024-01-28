(ns mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [clojure.edn :as edn]
   [babashka.fs :as fs]
   [clojure.pprint :as pprint]))

;; TO-DO
;; rydde i css-filen, spesielt rundt "wall" klassen
;; rydde opp i page, spesielt rundt random og reverse logikken
;; splitte i flere navnerom, eks view, store

;; URLOG skrives ved å legge på ett og ett element i text/urlog/urls.edn.
;;
;; text/urlog/urls.edn ser ut:

{:urlog/docs
 [{:doc/slug "urlog-1",
   :urlog/url "https://www.my90stv.com/"}
  {:doc/slug "urlog-2",
   :urlog/url "https://bezier.method.ac/",}
  {:doc/slug "urlog-3",
   :urlog/url "https://grids.obys.agency/",}
  ,,, #_ "... og så mange flere URL-er ..."
  ]}

;; TAGS
;;
;; Neno ønsker tag-støtte til URLOG.
;; Vi ser for oss at en URL kan ha en :urlog/tags, som er et sett.
;; Utover det har vi ikke en bestemt plan for datamodellering.

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

  :rcf)

(defn update-urls!
  "Update each url with a function

  Will update URL file on disk. You can run it from a REPL to migrate data, then
  commit your changes."
  [f]
  (let [old (edn/read-string (slurp urlogfile-path))
        new (update old :urlog/docs (comp vec #(map f %)))]
    (spit urlogfile-path
          (binding [*print-namespace-maps* false
                    pprint/*print-right-margin* 200]
            (with-out-str (pprint/pprint new))))))

(comment

  ;; DATA MIGRATION
  ;; When tags are empty, omit the :urlog/tags key
  (update-urls! (fn [u]
                  (if (seq (:urlog/tags u))
                    u
                    (dissoc u :urlog/tags))))

  :rcf
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
         (let [url (:urlog/url doc)
               door (select-door url (:doors assets))]
           [:div {:class :wall :role :none}
            (wall->html (:wall assets))
            (door+url->html door url)
            (wall->html (:wall assets))]))]])))
