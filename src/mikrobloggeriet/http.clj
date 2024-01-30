(ns mikrobloggeriet.http)

(defn permanent-redirect [{:keys [target]}]
  (assert target)
  {:status 308
   :headers {"Location" target}})

(defn response-ok? [response]
  (= 200 (:status response)))

(defn path-param
  "Get a path parameter from an HTTP request.

  Supports Compojure style parameters and Reitit style parameters."
  [req param]
  (or (get-in req [:route-params param]) ;; compojure
      (get-in req [:path-params param]) ;; reitit
      ))
