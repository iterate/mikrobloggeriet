;; # Database for Mikrobloggeriet?

(ns mikrobloggeriet.teodor.db
  (:require
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

#_
(def
  "Does not yet work!"
  config
  {:host "127.0.0.1"
   :port 10140
   :user "test"
   :password "test"
   :database "test"})

#_
(defonce conn (pg/connect config))

(comment


  )
