(ns mikrobloggeriet.feed-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.feed :as feed]))

(def olorm-1
  {:doc/slug "olorm-1",
   :doc/created "2023-04-17",
   :doc/uuid "c29d08c8-6689-4ecc-b8f3-2018f285da96",
   :git.user/email "lars.barlindhaug@iterate.no",
   :doc/markdown
   "# En god natts søvn\n\nI går holdt jeg på en litt kompleks refaktorering hvor jeg skal hente fargevarianter for en garnpakke fra to forskjellige steder i databasen, avhengig av om de er opprettet av designeren av oppskriften eller strikkeren har laget sin egen fargevariant. Det endte med at jeg ikke ble ferdig før jeg måtte gå hjem, og jeg så for meg at jeg trengte å bruke et par timer på det i dag. Hadde jeg blitt på jobb hadde jeg nok også lett brukt et par timer.\n\nNår jeg kom på jobb i dag så jeg umiddelbart hva som var feil, et lite stykke unna der jeg jobbet i går, og fikset alt på ca 5 minutter.\n"})

(deftest generate-feed-test
  (is (= [:feed
          [:title "Mikrobloggeriet"]
          '([:entry
             [:title "En god natts søvn"]
             [:published "2023-04-17"]])]
         (feed/generate-feed [olorm-1]))))

(comment
  (require '[datomic.api :as d])
  (def olorm-1 (d/entity mikrobloggeriet.state/datomic
                         [:doc/slug "olorm-1"]))

  )
