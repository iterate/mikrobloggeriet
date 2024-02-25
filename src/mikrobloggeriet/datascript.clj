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

(def db (load-cohorts [store/vakt]))

(into {} (d/entity db [:author/email "olav.moseng@iterate.no"]))
;; => {:author/email "olav.moseng@iterate.no", :author/first-name "Olav"}

(d/q '[:find ?name
       :where [?cohort :author/first-name ?name]]
     db)
;; => #{["Olav"] ["Julian"] ["Teodor"] ["Neno"]}
