(ns mikrobloggeriet.teodor.markdown-compare
  (:require
   [nextjournal.clerk :as clerk]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform]
   [mikrobloggeriet.pandoc :as pandoc]))

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
