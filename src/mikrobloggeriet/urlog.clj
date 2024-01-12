(ns mikrobloggeriet.urlog
  (:require
   [hiccup.page :as page]
   [clojure.string :as str]))

(defn logo []
  [:pre {:class :logo}
   (slurp "src/mikrobloggeriet/urlog_assets/logo.txt")])

(defn door [door-path url]
  [:div {:class :wall}
   [:pre (slurp "src/mikrobloggeriet/urlog_assets/wall.txt")]
   [:div {:class :component}
    [:pre "_|____|____|____|"]
    [:a {:href url :class :door}
     [:pre {:class :closed}
      (slurp (str door-path "closed.txt"))]
     [:pre {:class :open}
      (slurp (str door-path "open.txt"))]]]
   [:pre (slurp "src/mikrobloggeriet/urlog_assets/wall.txt")]])

(def door-paths
  (let [doors-dir "src/mikrobloggeriet/urlog_assets/doors/"]
    [(str doors-dir "door1/")
     (str doors-dir "door2/")
     (str doors-dir "door3/")
     (str doors-dir "door4/")
     (str doors-dir "door5/")]))

(defn rand-door [url]
  (door (rand-nth door-paths) url))

(def urlfile-path "text/urlog3/urls.txt")
(defn parse-urlfile
  "Parse an urlfile into a vector of urls (strings).

  - Empty lines are ignored
  - Lines starting with # or whitespace then # are treated as comments
"
  [s]
  (->> (str/split-lines (str/trim s))
       (map str/trim)
       (remove str/blank?)
       (remove #(str/starts-with? % "#"))))

(comment
  (def urls-edn
    (let [urls (parse-urlfile (slurp urlfile-path))
          slugs (map (partial str "urlog-") (map inc (range)))]
      {:urlog/docs
       (into []
             (map (fn [url slug]
                    {:doc/slug slug
                     :urlog/url url
                     :urlog/tags #{}})
                  urls slugs))}))

  (spit "text/urlog3/urls.edn"
        (binding [*print-namespace-maps* false]
          (with-out-str (clojure.pprint/pprint urls-edn)))))

(comment
  (reverse (parse-urlfile (slurp urlfile-path))))

(defn index-section [_req slug]
  [:section
   [:h2 "URLOG"]
   [:p "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]
   [:p [:a {:href slug} "Gå inn i huset –> 🏨"]]])

(defn feeling-lucky [content]
  [:a {:href "/random-doc" :class :feeling-lucky} content])

(defn urlogs [_req]
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
     (-> (logo))
     [:p {:class "intro"}
      "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]]
    [:div {:class "all-doors"}
     (for [url (reverse (parse-urlfile (slurp urlfile-path)))] (-> (rand-door url)))]]))
