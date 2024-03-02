(ns mikrobloggeriet.teodor.traffic
  (:require
   [mikrobloggeriet.repl :as repl]
   [nextjournal.clerk :as clerk]
   [pg.core :as pg]))

(defonce conn (:mikrobloggeriet.system/db @repl/state))

(comment
  (alter-var-root #'conn (constantly (:mikrobloggeriet.system/db @repl/state)))

  )

(clerk/table
 (pg/query conn "select * from access_logs"))

;; # Sync for access logs
;;
;; Motivation.
;; We want to explore the traffic to Mikrobloggeriet.
;; To do that, we need to:
;;
;; - be able to extract a batch from the traffic logs
;; - insert that batch of traffic into local traffic logs

(defn dump-traffic-logs [conn]
  (pg/query conn "select * from access_logs"))

(comment
  ;; Backup the whole db
  (doseq [log-line (dump-traffic-logs conn)]
    (when-let [{:keys [id]} log-line]
      (spit (str ".local/access-logs-" id ".edn")
            log-line)))
  )

;; what is a good batch size?

(count (dump-traffic-logs conn))

;; 29 lines -> 116K storage.

;; mikrobloggeriet: 353 630 lines

(double
 (* 116 (/ 353630
           29)))

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
