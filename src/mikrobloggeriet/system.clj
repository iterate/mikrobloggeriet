(ns mikrobloggeriet.system
  (:require [integrant.core :as ig]))

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


  )
