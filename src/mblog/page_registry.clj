(ns mblog.page-registry
  (:require
   [hiccup.page]
   [mblog.indigo :as indigo]
   [mblog.ui.doc :as ui.doc]))

(defn define-page [page]
  (when (not (:pagemaker/render page))
    (throw (ex-info "Invalid page: :page/render function not set." {:page page})))
  page)

(def registry
  {:page/indigo
   (define-page
     {:pagemaker/prepare-data #'indigo/req->innhold
      :pagemaker/render #'indigo/innhold->hiccup})

   :page/doc
   (define-page
     {:pagemaker/prepare-data #'ui.doc/req->doc
      :pagemaker/render #'ui.doc/doc->hiccup})})
