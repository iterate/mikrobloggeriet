(ns mikrobloggeriet.urlog2
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [hiccup.page :as page]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.doc-meta :as doc-meta]
   [mikrobloggeriet.store :as store]))

;; urlog --- men uten markdown.

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

(docs store/urlog2)
(store/doc-folder store/urlog2 (first (docs store/urlog2)))

(defn urlogs [_req]
  (page/html5
      [:head (page/include-css "/urlog.css")]
    [:body
     [:p [:a {:href "/random-doc" :class :feeling-lucky} "🎄"]]
     [:main
      [:p (str/join " "
                    (for [doc (docs store/urlog2)]
                      (doc/slug doc)))]
      [:p
       (let [cohort store/urlog]
         (interpose " · "
                    (for [doc (->> (store/docs cohort)
                                   (map (fn [doc] (store/load-meta cohort doc)))
                                   (remove doc-meta/draft?))]
                      [:a {:href (store/doc-href cohort doc)} "🚪"])))]]]))
