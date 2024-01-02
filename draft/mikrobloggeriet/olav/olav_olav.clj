(ns mikrobloggeriet.olav.olav-olav
  (:require [nextjournal.clerk :as clerk]
            [mikrobloggeriet.store :as store]
            [mikrobloggeriet.serve :as serve]
            [clojure.string :as str]))

(let [cohort store/luke]
  (clerk/table (map (fn [doc]
                      (merge doc
                             {:title (:title (serve/markdown->html+info (slurp (store/doc-md-path cohort doc))))
                              :author (store/author-first-name cohort doc)
                              :href (store/doc-href cohort doc)
                              :html (clerk/html [:a {:href (store/doc-href cohort doc)} (store/author-first-name cohort doc)])}))
                    (store/docs cohort))))

(defn cohort-authors-href [cohort]
  (map (fn [doc]
         {:author (store/author-first-name cohort doc)
          :href (store/doc-href cohort doc)})
       (store/docs cohort)))


(defn markdown-links [title-fn href-fn elements]
  (str/join ", " (map (fn [x]
                        (str "[" (title-fn x) "]" "(" (href-fn x) ")"))
                      elements)))

(let [md-links (markdown-links :author :href (cohort-authors-href store/luke))]
  (clerk/fragment (clerk/md md-links)
                  (clerk/html [:code md-links])))

(comment
  (clerk/clear-cache!))
