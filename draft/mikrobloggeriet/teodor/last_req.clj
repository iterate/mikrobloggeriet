{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(ns mikrobloggeriet.teodor.last-req
  (:require
   [nextjournal.clerk :as clerk]))

;; ## Last HTTP request

^{::clerk/sync true}
(defonce last-tapped (atom nil))

(comment
  (tap> (rand))
  @last-tapped

  )

(defn http-request? [m]
  (and (contains? m :headers)
       (contains? m :request-method)))

(defn sanitize-http-request [req]
  (dissoc req :async-channel))

(defn store-tapped [val]
  (when (http-request? val)
    (reset! last-tapped (sanitize-http-request val))))

(add-tap #'store-tapped)

{::clerk/visibility {:code :show :result :show}}

^{::clerk/budget nil ::clerk/auto-expand-results? true}
@last-tapped

^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
