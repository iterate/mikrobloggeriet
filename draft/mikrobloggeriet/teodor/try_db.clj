;; # Database for Mikrobloggeriet?

(ns mikrobloggeriet.teodor.try-db
  (:require
   [mikrobloggeriet.config :as config]
   [nextjournal.clerk :as clerk]
   [pg.core :as pg]
   [mikrobloggeriet.store :as store]
   [clojure.string :as str]))

;; skal vi ha tilstand?
;; Det har fordeler :)

;; la oss prøve litt.

;; Vi går med https://github.com/igrishaev/pg2.
;; Hvorfor?
;;
;; - next.jdbc er flott
;; - men jeg har lyst til å kunne jobbe mot /rå postgres/.
;;   Ikke bare gjøre "generell SQL".

(def config
  {:host "localhost"
   :port config/pg-port
   :user "mikrobloggeriet"
   :password "mikrobloggeriet"
   :database "mikrobloggeriet"})

(defonce conn (pg/connect config))

(pg/query conn "select 1 as one")

(defn uri->cohort-slug [uri]
  (-> uri
      (str/split #"/")
      second))

(def cohort-slugs
  (->> store/cohorts
       vals
       (map :cohort/slug)
       (into #{})))

(clerk/table
 (->> (pg/query conn "select method, id, uri, timestamp from access_logs")
      (filter #(contains? cohort-slugs (uri->cohort-slug (:uri %))))))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
