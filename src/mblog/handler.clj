(ns mblog.handler
  (:require [hiccup.page]
            [mblog.indigo :as indigo]
            [mblog.ui.doc :as ui.doc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SIDER

(defn define-page [page]
  (when (not (:pagemaker/render page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def indigo-page
  (define-page
    {:pagemaker/prepare-data #'indigo/req->innhold
     :pagemaker/render #'indigo/innhold->hiccup}))

(def doc-page
  (define-page
    {:pagemaker/prepare-data #'ui.doc/req->doc
     :pagemaker/render #'ui.doc/doc->hiccup}))

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

  @last-req

  )
