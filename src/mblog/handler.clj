(ns mblog.handler
  (:require [hiccup.page]
            [mblog.indigo :as indigo]
            [mblog.ui.doc :as ui.doc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SIDER

(def indigo-page
  {:page/prepare-data #'indigo/req->innhold
   :page/render #'indigo/innhold->hiccup})

(def doc-page
  {:page/prepare-data #'ui.doc/req->doc
   :page/render #'ui.doc/doc->hiccup})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MACHINERY
;;
;; Job to be done: make adding pages a smooth experience.

(defn prepare-data [req page]
  (when-let [prepare-fn (:page/prepare-data page)]
    (prepare-fn req)))

(defn render [data page]
  ((:page/render page) data))

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
  (-> @last-req :reitit.core/match :path-params :slug)
  ;; => "olorm-1"
  (ui.doc/req->doc @last-req)

  (prepare-data indigo-page @last-req)

  (def r1 @last-req)

  )
