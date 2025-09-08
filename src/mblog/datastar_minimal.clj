(ns mblog.datastar-minimal
  {:doc "Copied from the Datastar HTTP-kit example

https://github.com/starfederation/datastar-clojure/blob/76041542602cd5f7838f39cf190782e1d2e53bb6/examples/hello-httpkit/src/hello_httpkit.clj"}
  (:require
   [cheshire.core]
   [hiccup2.core]
   [org.httpkit.server]
   [reitit.ring.middleware.parameters]
   [reitit.ring]
   [ring.util.response]
   [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open]]
   [starfederation.datastar.clojure.api :as d*]))

(defn get-signals [req]
  (-> req d*/get-signals cheshire.core/parse-string))

(def home-page
  (str "<!DOCTYPE html>\n"
       (hiccup2.core/html
           [:html {:lang "en"}
            [:head
             [:title "Datastar SDK Demo"]
             [:script {:type "module", :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@main/bundles/datastar.js"}]]
            [:body
             [:div
              {:data-signals-delay "345"}
              ;; {:datastar/signals {"delay" 345}}
              [:div
               [:h1 "Datastar SDK Demo"]]
              [:p "SSE events will be streamed from the backend to the frontend."]
              [:div [:label {:for "delay"} "Delay in milliseconds"]
               [:input#delay {:data-bind-delay "" :type "number", :step "100", :min "0"}]]
              ;; {:datastar/bind {:delay ""}}
              [:button {:data-on-click "@get('/dsminimal-messsage')"} "Start"]]
             [:div#message "Hello, world!"]]])))

(defn home [_req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body home-page})

(def message "Hello, world!")

(defonce !req (atom nil))

(comment
  (keys @!req)
  (:params @!req)
  )

(defn hello-world [request]
  (reset! !req request)
  (let [d (-> request get-signals (get "delay") int)]
    (->sse-response request
                    {on-open
                     (fn [sse]
                       (d*/with-open-sse sse
                         (dotimes [i (count message)]
                           (d*/patch-elements! sse (str (hiccup2.core/html
                                                            [:div {:id "message"}
                                                             (subs message 0 (inc i))])))
                           (Thread/sleep d))))})))
