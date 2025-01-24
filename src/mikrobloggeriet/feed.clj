(ns mikrobloggeriet.feed
  (:require
   [hiccup2.core :as hiccup]
   [mikrobloggeriet.doc :as doc]))

(defn generate-feed [docs]
  [:feed
   [:title "Mikrobloggeriet"]
   (for [doc docs]
     (into [:entry]
           (concat
            [[:title (doc/title doc)]
             [:published (:doc/created doc)]]
            (when-let [author-name (some-> doc :doc/primary-author :author/first-name)]
              [[:author [:name author-name]]])
            [[:summary {:type "html"} (doc/description doc)]
             [:content {:type "html"} (doc/html doc)]])))])

(defn serialize [feed]
  (str (hiccup/html {:mode :xml}
         (list (hiccup/raw "<?xml version=\"1.0\" encoding=\"utf-8\"?>")
               feed))))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "application/xml"}
   :body (serialize (generate-feed (->> (doc/all (:mikrobloggeriet.system/datomic req))
                                        (remove :doc/draft?))))})

(comment
  (require '[mikrobloggeriet.state])
  (handler {:mikrobloggeriet.system/datomic mikrobloggeriet.state/datomic})
  )
