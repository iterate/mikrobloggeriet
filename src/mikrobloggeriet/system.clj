(ns mikrobloggeriet.system
  (:require
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.serve :as serve]
   [mikrobloggeriet.state :as state]
   [org.httpkit.server :as httpkit]))

(defn create-datomic [_previous]
  (db/loaddb db/cohorts db/authors))
#_(alter-var-root #'state/datomic create-datomic)

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
;; HTTP-serveren henger sammen med REPL-serveren.

(defn ^:export start! [{:keys [port]}]
  (alter-var-root #'state/datomic create-datomic)
  (alter-var-root #'state/injected-app create-injected-app)
  (alter-var-root #'state/http-server (create-http-server (or port 7223))))
#_(start! {})
