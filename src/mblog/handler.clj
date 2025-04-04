(ns mblog.handler
  (:require
   [hiccup.page]
   [mblog.page-registry]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MACHINERY
;;
;; Job to be done: make adding pages a smooth experience.

(defn prepare-data [req page]
  (when-let [prepare-fn (:pagemaker/prepare-data page)]
    (prepare-fn req)))

(defn render [data page]
  ((:pagemaker/render page) data))

(defn http-response [hiccup]
  (when hiccup
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body
     (hiccup.page/html5 {} hiccup)}))

(def last-req "Last HTTP request received, for REPL debugging"
  (atom nil))

(defn handle [req page]
  (reset! last-req req)
  (-> req
      (prepare-data page)
      (render page)
      http-response))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REPL-examples

(comment

  (keys @last-req)

  (-> @last-req :reitit.core/match :data :name)

  )
