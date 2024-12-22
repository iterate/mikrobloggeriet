(ns mikrobloggeriet.system
  (:require
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.serve :as serve]
   [mikrobloggeriet.state :as state]
   [org.httpkit.server :as httpkit]))

(defn create-datomic [_previous]
  (db/loaddb db/cohorts db/authors))
#_(alter-var-root #'state/datomic create-datomic)

(defn create-app [_previous]
  (serve/assemble-app))
#_(alter-var-root #'state/app create-app)

(defn create-http-server [port]
  (fn [previous]
    (when previous
      (httpkit/server-stop! previous))
    (httpkit/run-server (fn [req]
                          (-> req
                              (assoc ::datomic state/datomic)
                              state/app))
                        {:port port
                         :legacy-return-value? false})))
#_(alter-var-root #'state/http-server (create-http-server 7223))
;; Obs: kan ikke restarte HTTP-serveren når vi kjører med `garden run`, fordi
;; HTTP-serveren henger sammen med REPL-serveren på et vis. Resten av systemet
;; kan fint startes på nytt.

(defn ^:export start! [{:keys [port]}]
  (alter-var-root #'state/datomic create-datomic)
  (alter-var-root #'state/app create-app)
  (alter-var-root #'state/http-server (create-http-server (or port 7223))))
#_(start! {})
