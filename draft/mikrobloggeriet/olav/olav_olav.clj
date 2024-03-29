(ns mikrobloggeriet.olav.olav-olav
  (:require [nextjournal.clerk :as clerk]
            [mikrobloggeriet.store :as store]
            [mikrobloggeriet.serve :as serve]
            [clojure.string :as str]))

(defonce state (atom {}))
@state

(comment
  (swap! state assoc :name "olav"))


(defonce counter (atom 0))
@counter

(comment
  (swap! counter inc)

  (reset! counter 0))



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

(defn doc-date [cohort]
  (map (fn [doc]
         (merge doc
                {:author (store/author-first-name cohort doc)}
                {:doc/created (:doc/created (store/load-meta cohort doc))}))
       (store/docs cohort)))

(clerk/table (flatten (map (fn [c] (doc-date c)) (vals store/cohorts))))


(comment
  (clerk/clear-cache!))

