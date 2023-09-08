(ns mikrobloggeriet.http)

(defn temporary-redirect [{:keys [target]}]
  (assert target)
  {:status 301
   :headers {"Location" target}})

(defn permanent-redirect [{:keys [target]}]
  (assert target)
  {:status 308
   :headers {"Location" target}})
