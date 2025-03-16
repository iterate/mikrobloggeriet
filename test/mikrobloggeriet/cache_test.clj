(ns mikrobloggeriet.cache-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [mikrobloggeriet.cache :as cache]))

(deftest cache-fn-by
  (is (= ((cache/cache-fn-by (atom {})
                             (fn [a b] (+ a b))
                             (fn [a b] (str a " " b)))
          1 2)
         3)))

(def hildringstimen-markdown
  (str/trim "
# Hildringstimen

I hildringstimen er det godt å seile.
En kaffekjeft og stomp med sirup på.
Du åpner med et smell ditt rorhusvindu
og stikker nesa ut og snuser mot det blå!
Ja, se den gamle gullsmed, morgensolen,
har atter hamret havet til et fat
der Skaperen med ødselhet har drysset
en håndfull holmer som nå bader i karat.

Og se mot styrbord, gjennom hildringsdisen: –
de fjerne øyer svever! Fjell kan fly!
Nå aner du hva salig Adam skuet
da han ble purret ved det aller første gry.
I slikt et lys, da blir alt mørkt et minne,
du hviler hånden rolig mot ditt ratt
og gnukker tommelen mot midtskipsmerket,
og vet, for denne gang, din kurs var riktig satt.

Du møter kuttere på vei mot feltet,
et solbrent fjes, som spytter brunt i le.
En fiskerneve hilser fra et rorhus
og gjør din glade hjemreis dobbelt rik ved det.
For ennå er din skute langt av lande,
men aldri var deg mennesket mer nært
enn nettopp nå, i denne gyldne time
som lar deg fatte alt du har å holde kjært.

I hildringstimen er det godt å seile
og favne lys til kraft for blod og ben.
Og vite at, i netter som skal komme
så fins en kurs imot et land av sang og sten.
"))

(comment
  (cache/parse-markdown "tekst med _vekt_")

  (cache/parse-markdown hildringstimen-markdown)
  )

(deftest parse-markdown
  (testing "parses doc as html"
    (is (= (-> (cache/parse-markdown "tekst med _vekt_")
               :doc/html
               str/trim)
           "<p>tekst med <em>vekt</em></p>")))

  (testing "extracts title"
    (is (= (-> hildringstimen-markdown cache/parse-markdown :title)
           "Hildringstimen")))

  (testing "extracts first paragraph as description"
    (is (= (-> hildringstimen-markdown cache/parse-markdown :description)
           (->>
            "
I hildringstimen er det godt å seile.
En kaffekjeft og stomp med sirup på.
Du åpner med et smell ditt rorhusvindu
og stikker nesa ut og snuser mot det blå!
Ja, se den gamle gullsmed, morgensolen,
har atter hamret havet til et fat
der Skaperen med ødselhet har drysset
en håndfull holmer som nå bader i karat.
"
            (str/trim)
            (str/split-lines)
            (str/join " "))))))
