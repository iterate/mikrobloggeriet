(ns mikrobloggeriet.urlog3
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]))

;; like urlog2 -- but all urls in one file

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
  (parse-urlfile (slurp urlfile-path))
  )

(defn doc
  [req cohort]
  (when (:slug (:route-params req))
    (let [doc (doc/from-slug (:slug (:route-params req)))
          {:keys [title doc-html]}
          (when (store/doc-exists? cohort doc)
            (markdown->html+info (slurp (store/doc-md-path cohort doc))))]
      {:status 200
       :body
       (page/html5
        (into [:head] (concat (when title [[:title title]])
                              (html-header req)))
        [:body
         [:p (feeling-lucky "🎄")
          " — "
          [:a {:href "/"} "mikrobloggeriet"]
          " "
          [:a {:href (str "/" (cohort/slug cohort) "/")}
           (cohort/slug cohort)]
          " — "
          [:span (let [
                       previouse-number (dec (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span [:a {:href (str (store/doc-href cohort prev))} (doc/slug prev)] " · "]))]
          [:span (:doc/slug doc) ]
          [:span (let [previouse-number (inc (doc/number doc))
                       prev (doc/from-slug (str (cohort/slug cohort) "-" previouse-number))]
                   (when (store/doc-exists? cohort prev)
                     [:span " · " [:a {:href (str (store/doc-href cohort prev))}  (doc/slug prev)]]
                     ))]]
         doc-html])})))

(defn urlogs [_req]
  (page/html5 [:head (page/include-css "/urlog.css")]
    [:body [:main
            [:p (interpose " " (for [url (parse-urlfile (slurp urlfile-path))]
                                [:a {:href url
                                     :target "_blank"}
                                "🚪"]))]]]))
