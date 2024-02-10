(ns mikrobloggeriet.http)

(defn permanent-redirect
  "Permanent redirect to target"
  [{:keys [target]}]
  (assert target)
  {:status 308
   :headers {"Location" target}})

(defn response-ok? [response]
  (= 200 (:status response)))

(defn path-param
  "Get a path parameter from an HTTP request."
  [req param]
  (get-in req [:path-params param]))
