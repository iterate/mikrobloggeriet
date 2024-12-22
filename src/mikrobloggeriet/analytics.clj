(ns mikrobloggeriet.analytics
  (:require
   [cljc.java-time.instant :as instant]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.local-time :as lt]
   [cljc.java-time.zone-id :as zone-id]
   [cljc.java-time.zoned-date-time :as zdt]
   [duratom.core :refer [duratom]])
  (:import
   [java.time Instant]))

(defn instant->zdt [instant]
  (instant/at-zone instant (zone-id/of "Europe/Oslo")))

(defn instant->ldt [instant]
  (zdt/to-local-date-time (instant->zdt instant)))

(defn instant->local-date-str [instant]
  (-> instant instant->ldt ldt/to-local-date str))
#_(instant->local-date-str (instant/now))

(defonce !pageviews
  (when-let [storage-path (System/getenv "GARDEN_STORAGE")]
    (duratom :local-file
             :file-path (str storage-path "/mikrobloggeriet.pageviews.edn")
             :commit-mode :sync
             :init {})))
#_(deref !pageviews)

(defn rf [state {:as request :keys [uri]}]
  (if-let [now (:mikrobloggeriet.system/now request)]
    (update-in state [uri (instant->local-date-str now)] (fnil inc 0))
    state))

(defn consume! [req]
  (swap! !pageviews rf req)
  req)
#_(dotimes [_ 5] (consume! {:uri "/urlog/" :mikrobloggeriet.system/now (Instant/parse "2024-12-22T18:20:08.542820Z")}))
