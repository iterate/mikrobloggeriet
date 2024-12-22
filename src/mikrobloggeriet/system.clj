(ns mikrobloggeriet.system
  (:require
   [datomic.api :as d]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.serve :as serve]
   [mikrobloggeriet.state :as state]
   [nextjournal.beholder :as beholder]
   [org.httpkit.server :as httpkit]))

(defn create-datomic [_previous]
  (db/loaddb {:cohorts db/cohorts :authors db/authors}))
#_(alter-var-root #'state/datomic create-datomic)

(defn create-file-watcher [db]
  (fn [previous]
    (when previous
      (beholder/stop previous))
    ;; We watch for changes in local development only, because we don't modify
    ;; documents in-place in production.
    (when-not (System/getenv "GARDEN_GIT_REVISION")
      (let [roots (map :cohort/root (cohort/all-cohorts db))]
        (apply beholder/watch
               (fn [_event]
                 ;; NOTE: Current reloading behavior is "when ANY doc is
                 ;; changed, reload EVERY doc". Performance can be improved, but
                 ;; let's get it correct first.
                 (let [the-docs (->> (cohort/all-cohorts db)
                                     (mapcat db/find-cohort-docs))]
                   (alter-var-root #'state/datomic
                                   (fn [olddb]
                                     (-> olddb
                                         (d/with the-docs)
                                         :db-after)))))
               roots)))))
#_(alter-var-root #'state/file-watcher (create-file-watcher state/datomic))

(comment
  (def db state/datomic)
  (->> (cohort/all-cohorts db)
       (map :cohort/root))
  (def roots (apply beholder/watch tap> roots))
  (def olorm (d/entity db [:cohort/id :cohort/olorm]))
  (def the-docs (db/find-cohort-docs olorm))
  (-> db (d/with the-docs) :db-after)

  )

(defn create-injected-app [_previous]
  (let [handler (serve/create-ring-handler)]
    (fn [req]
      (-> req
          (assoc ::datomic state/datomic)
          handler))))
#_(alter-var-root #'state/injected-app create-injected-app)

(defn create-http-server [port]
  (fn [previous]
    (when previous
      (httpkit/server-stop! previous))
    (httpkit/run-server (fn [req]
                          (state/injected-app req))
                        {:port port
                         :legacy-return-value? false})))
#_(alter-var-root #'state/http-server (create-http-server 7223))

;; Obs: kan ikke restarte HTTP-serveren når vi kjører med `garden run`, fordi
;; HTTP-serveren henger sammen med REPL-serveren. Hvis du prøver å restarte
;; HTTP-servern i prod, vil du krasje prod (som vil føre til en restart, som er
;; helt OK, men også kan gjøres med `garden restart`).

(defn ^:export start! [{:keys [port]}]
  (alter-var-root #'state/datomic create-datomic)
  (alter-var-root #'state/file-watcher (create-file-watcher state/datomic))
  (alter-var-root #'state/injected-app create-injected-app)
  (alter-var-root #'state/http-server (create-http-server (or port 7223))))
#_(start! {})
