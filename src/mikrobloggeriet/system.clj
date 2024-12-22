(ns mikrobloggeriet.system
  (:require
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.serve :as serve]
   [mikrobloggeriet.state :as state]
   [nextjournal.beholder :as beholder]
   [org.httpkit.server :as httpkit]))

(defn create-datomic [_previous]
  (db/loaddb {:cohorts db/cohorts :authors db/authors}))
#_(alter-var-root #'state/datomic create-datomic)

(defn create-cohort-watchers [db]
  (fn [previous]
    (when (seq previous)
      (run! beholder/stop (vals previous)))
    ;; We only want to run the wathcer in local developement, eg when we're not
    ;; running behind a specific Git revision. When we _are_ running a specific
    ;; Git revision, we assume our source files are static.
    ;;
    ;; Does not catch users adding new cohorts. In that case, reload Datomic by
    ;; hand.
    (when-not (System/getenv "GARDEN_GIT_REVISION")
      (prn :hello)
      (->> (for [cohort (cohort/all-cohorts db)]
             [(:cohort/id cohort)
              (beholder/watch (fn [_]
                                (tap> cohort))
                              (doto (:cohort/root cohort) prn))])
           (into {})))))
#_ (alter-var-root #'state/cohort-watchers (create-cohort-watchers state/datomic))

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
  (alter-var-root #'state/cohort-watchers (create-cohort-watchers state/datomic))
  (alter-var-root #'state/injected-app create-injected-app)
  (alter-var-root #'state/http-server (create-http-server (or port 7223))))
#_(start! {})
