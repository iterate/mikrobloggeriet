(ns mikrobloggeriet.http)

(defn permanent-redirect
  "Permanent redirect to target.

  The :body \"\" part is kept to ease over a difference between Compojure and
  Reitit during the router migration of 2024-01."
  [{:keys [target]}]
  (assert target)
  {:status 308
   :headers {"Location" target}
   :body ""})

(defn response-ok? [response]
  (= 200 (:status response)))

(defn path-param
  "Get a path parameter from an HTTP request.

  Supports Compojure style parameters and Reitit style parameters."
  [req param]
  (or (get-in req [:route-params param]) ;; compojure
      (get-in req [:path-params param]) ;; reitit
      ))
