(ns mikrobloggeriet.teodor.markdown-compare
  (:require
   [mikrobloggeriet.pandoc :as pandoc]
   [mikrobloggeriet.store :as store]
   [nextjournal.clerk :as clerk]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform]))

(def text1
  "> et tout autour, la longue cohorte de ses personnages, avec leur histoire, leur passé, leurs légendes:
> 1. Pélage vainqueur d'Alkhamah se faisant couronner à Covadonga
> 2. La cantatrice exilée de Russie suivant Schönberg à Amsterdam
> 3. Le petit chat sourd aux yeux vairons vivant au dernier étage
> 4. ...

**Georges Perec**, _La Vie mode d'emploi_.

---
")

(def text2
  "> et tout autour, la longue cohorte de ses personnages, avec leur histoire, leur passé, leurs légendes:
>
> 1. Pélage vainqueur d'Alkhamah se faisant couronner à Covadonga
> 2. La cantatrice exilée de Russie suivant Schönberg à Amsterdam
> 3. Le petit chat sourd aux yeux vairons vivant au dernier étage
> 4. ...

**Georges Perec**, _La Vie mode d'emploi_.

---
")

(def convert-nextjournal
  (comp nextjournal.markdown.transform/->hiccup nextjournal.markdown/parse))

(def convert-pandoc
  (comp pandoc/to-html pandoc/from-markdown))

(clerk/example
  (-> text1 convert-nextjournal clerk/html)
  (-> text1 convert-pandoc clerk/html)

  (-> text2 convert-nextjournal clerk/html)
  (-> text2 convert-pandoc clerk/html)
  )

;; så, forskjellig konvensjon for når en punktliste starter, hvorvidt man skal
;; kreve mellomrom eller ikke for å bryte opp overgangen mellom et avsnitt og en
;; punktliste. Nice, TIL!

;; hva med em-dash?

(clerk/html (convert-pandoc "Dette er alt---la oss gå videre!"))

;; funker som jeg liker i pandoc ☝️

;; hva med Nextjournal/markdown?

(clerk/html (convert-nextjournal "Dette er alt---la oss gå videre!"))

;; nope, her trenger vi unicode em-dashes.

(clerk/html (convert-nextjournal "Dette er alt—la oss gå videre!"))

;; mmmmm.

;; hvordan representerer pandoc em-dasher?

(pandoc/from-markdown "Dette er alt---la oss gå videre!")

;; ikke noe spesielt type element nei, bare en rå —.
;; Så det skjer på vei _inn_ fra markdown, ikke på vei ut.

(defn timed* [f]
  (let [start (System/currentTimeMillis)
        result (f)
        end (System/currentTimeMillis)]
    {:duration (- end start)
     :result result}))

(defmacro timed [& body]
  `(timed* (fn [] ~@body)))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  (-> {}
      (assoc :pandoc (timed (convert-pandoc "Dette er alt---la oss gå videre!")))
      (assoc :nextjournal (timed (convert-nextjournal "Dette er alt---la oss gå videre!"))))
  ;; => {:pandoc {:duration 84, :result "<p>Dette er alt—la oss gå videre!</p>\n"},
  ;;     :nextjournal
  ;;     {:duration 1, :result [:div [:p "Dette er alt---la oss gå videre!"]]}}
  )

(def doc (slurp "o/olorm-7/index.md"))

(-> {}
    (assoc :pandoc (timed (convert-pandoc doc)))
    (assoc :nextjournal (timed (convert-nextjournal doc))))

;; 40 ms vs 283 ms.
;; Forbedring, det!
;;
;; Så (omtrent som forventet):
;;
;; 1. Veldig stor forskjell for små dokumenter
;; 2. Nextjournal/markdown er fremdeles raskere for store dokumenter.
;;
;; Men nå har vi benchmarks.

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
