(ns mblog.handler
  (:require [mblog.indigo :as indigo]
            [mblog.ui.doc :as ui.doc]))

(def last-req (atom nil))

(require 'hiccup.page)

(defn indigo [req]
  (reset! last-req req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   (hiccup.page/html5 {}
     (-> req indigo/req->innhold indigo/innhold->hiccup))})

(defn doc [req]
  (reset! last-req req)
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   (hiccup.page/html5 {}
     (-> req ui.doc/req->doc ui.doc/doc->hiccup))})

(comment
  (keys @last-req)
  (-> @last-req :reitit.core/match :path-params :slug)
  ;; => "olorm-1"
  (ui.doc/req->doc @last-req)
  )
