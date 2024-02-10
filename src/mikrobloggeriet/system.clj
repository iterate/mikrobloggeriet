(ns ^{:experimental true
      :doc "Try out using Integrant for system state"}
    mikrobloggeriet.system
  (:require
   [integrant.core :as ig]
   [malli.core :as m]
   [mikrobloggeriet.config :as config]
   [mikrobloggeriet.db :as db]
   [pg.core :as pg]))

;; og Integrant?

(def config
  {:mikrobloggeriet/person {:name (str (rand-nth '(jack joe oleg))
                                       "-"
                                       (rand-int 100))}
   :mikrobloggeriet/greeter {:message "hello"
                             :person (ig/ref :mikrobloggeriet/person)}
   :mikrobloggeriet/terminator {:message "TERMINATE"
                                :person (ig/ref :mikrobloggeriet/person)}})

(defmethod ig/init-key :mikrobloggeriet/person
  [_ {}]
  {:name (str (rand-nth '(jack joe oleg))
              "-"
              (rand-int 100))})

(defmethod ig/init-key :mikrobloggeriet/greeter
  [_ {:keys [message person]}]
  (fn [] (str message " " (:name person))))

(defmethod ig/init-key :mikrobloggeriet/terminator
  [_ {:keys [message person]}]
  (fn [] (str message " " (:name person))))

(comment
  (defonce sys1 (ig/init config))

  (let [{:mikrobloggeriet/keys [greeter terminator]} sys1]
    [(greeter) (terminator)])
  ;; => ["hello joe-92" "TERMINATE joe-92"]

  (ig/halt! sys1)

  )

(defn dev+db []
  {::db {:host "localhost"
         :port config/pg-port
         :user "mikrobloggeriet"
         :password "mikrobloggeriet"
         :database "mikrobloggeriet"}})

(defmethod ig/init-key ::db
  [_ db-params]
  (let [db-conf (m/coerce db/Pg2Config db-params)]
    (pg/connect db-conf)))

(defmethod ig/halt-key! ::db
  [_ conn]
  (pg/close conn))

(comment
  (def sys2 (ig/init (dev+db)))

  sys2

  )
