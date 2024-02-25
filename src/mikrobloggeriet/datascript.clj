(ns mikrobloggeriet.datascript
  (:require
    [mikrobloggeriet.store :as store]
    [datascript.core :as d]))

(set! *print-namespace-maps* false)

;; En eksperimentell datascript-indeks for alt innholdet vårt

(store/all-cohort+docs)

(let [cohort store/olorm]
  (->> (store/docs cohort)
       (map (fn [doc] (store/load-meta cohort doc)))))

(def schema
  {:author/email {:db/unique :db.unique/identity}
   :cohort/slug {:db/unique :db.unique/identity}
   :doc/slug {:db/unique :db.unique/identity}
   :doc/uuid {:db/unique :db.unique/identity}
   :doc/chort {:db/cardinality :db.cardinality/one
               :db/valueType   :db.type/ref}
   :cohort/members {:db/cardinality :db.cardinality/many
                    :db/valueType   :db.type/ref}})

(defn load-cohorts [cohorts]
  (let [conn (d/create-conn schema)]
    (doseq [c cohorts]
      (d/transact! conn [c]))
    @conn))

;; Doesn't work:

#_
(def db (load-cohorts store/vakt))
