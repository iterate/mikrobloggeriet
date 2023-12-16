(ns mikrobloggeriet.urlog3
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [hiccup.page :as page]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.store :as store]))

;; like urlog2 -- but all urls in one file

(defn doc-exists?
  "True iff doc exists, does not care about content format."
  [cohort doc]
  (and (cohort/root cohort)
       (doc/slug doc)
       (fs/exists? (store/doc-meta-path cohort doc))))

(defn docs [cohort]
  (let [root (fs/file (cohort/repo-path cohort)
                      (cohort/root cohort))]
    (when (fs/directory? root)
      (->> (fs/list-dir (fs/file root))
           (map (comp doc/from-slug fs/file-name))
           (filter (partial doc-exists? cohort))
           (sort-by doc/number)))))

(comment
  (docs store/urlog2)
  (store/doc-folder store/urlog2 (first (docs store/urlog2)))
  :rcf)

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

(defn urlogs [_req]
  (page/html5 [:head (page/include-css "/urlog.css")]
    [:body [:main
            [:p (interpose " " (for [url (parse-urlfile (slurp urlfile-path))]
                                [:a {:href url
                                     :target "_blank"}
                                "🚪"]))]]]))
