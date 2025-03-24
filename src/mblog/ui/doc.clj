(ns mblog.ui.doc
  (:require
   [datomic.api :as d]
   [mikrobloggeriet.doc :as doc]))

(defn doc->hiccup [doc]
  [:pre (:doc/slug doc)]

  )

(defn req->doc [req]
  (d/entity (:mikrobloggeriet.system/datomic req)
            [:doc/slug (-> req :reitit.core/match :path-params :slug)]))

(comment
  (require 'mikrobloggeriet.state)
  (def olorm-1 (d/entity mikrobloggeriet.state/datomic
                         [:doc/slug "olorm-1"]))
  )
