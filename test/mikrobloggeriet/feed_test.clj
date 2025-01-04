(ns mikrobloggeriet.feed-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.feed :as feed]))

(def en-god-latter-forlenger-livet
  {:doc/slug "aforisme-3",
   :doc/created "2023-04-17",
   :doc/uuid "11dd8a76-4e43-4b00-923d-e41f27c730d9"
   :git.user/email "fiosof1@iterate.no",
   :doc/markdown
   "# En god latter forlenger livet

Jeg lover!!!"})

(deftest generate-feed-test
  (is (= [:feed
          [:title "Mikrobloggeriet"]
          '([:entry
             [:title "En god latter forlenger livet"]
             [:published "2023-04-17"]
             [:content {:type "html"}
              "<h1 id=\"en-god-latter-forlenger-livet\">En god latter forlenger\nlivet</h1>\n<p>Jeg lover!!!</p>\n"]])]
         (feed/generate-feed [en-god-latter-forlenger-livet]))))

(comment
  (require '[datomic.api :as d])
  (require '[mikrobloggeriet.doc :as doc])
  (def olorm-1 (d/entity mikrobloggeriet.state/datomic
                         [:doc/slug "olorm-1"]))
  (doc/html olorm-1)

  )
