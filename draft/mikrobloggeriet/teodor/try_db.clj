;; # Database for Mikrobloggeriet?

(ns mikrobloggeriet.teodor.try-db
  (:require
   [mikrobloggeriet.config :as config]
   [pg.core :as pg]))

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
