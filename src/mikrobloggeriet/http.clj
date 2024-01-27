(ns mikrobloggeriet.http)

(defn permanent-redirect [{:keys [target]}]
  (assert target)
  {:status 308
   :headers {"Location" target}})

(defn response-ok? [response]
  (= 200 (:status response)))

(defn temporary-redirect [{:keys [target]}]
  (assert target)
  {:status 301
   :headers {"Location" target}})
