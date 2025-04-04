(ns mblog.page-machinery
  (:require
   [hiccup.page]
   [mblog.page-registry]))

(defn prepare-data [req page]
  (when-let [prepare-fn (:pagemaker/prepare-data page)]
    (prepare-fn req)))

(defn render [data page]
  ((:pagemaker/render page) data))

(defn hiccup->response [hiccup]
  (when hiccup
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body
     (hiccup.page/html5 {} hiccup)}))

(def last-req "Last HTTP request received, for REPL debugging"
  (atom nil))

(defn respond [request page]
  (reset! last-req request)
  (-> request
      (prepare-data page)
      (render page)
      hiccup->response))
