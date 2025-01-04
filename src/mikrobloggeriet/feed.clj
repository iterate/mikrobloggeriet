(ns mikrobloggeriet.feed
  (:require
   [hiccup2.core :as hiccup]
   [mikrobloggeriet.doc :as doc]))

(defn generate-feed [docs]
  [:feed
   [:title "Mikrobloggeriet"]
   (for [doc docs]
     [:entry
      [:title (doc/title doc)]
      [:published (:doc/created doc)]])])

(defn serialize [feed]
  (str (hiccup/html {:mode :xml}
         (list (hiccup/raw "<?xml version=\"1.0\" encoding=\"utf-8\"?>")
               feed))))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "application/xml"}
   :body (serialize (generate-feed (doc/all (:mikrobloggeriet.system/datomic req))))})

(comment
  (handler {:mikrobloggeriet.system/datomic mikrobloggeriet.state/datomic})
  )
