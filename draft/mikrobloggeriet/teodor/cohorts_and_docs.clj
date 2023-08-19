;; # Kohorter og dokumenter - en ny abstraksjon

^{:nextjournal.clerk/toc true}
(ns mikrobloggeriet.teodor.cohorts-and-docs
  (:require
   clojure.repl
   [mikrobloggeriet.cohort :as cohort]
   [nextjournal.clerk :as clerk]))

;; Heisann!
;;
;; Jeg har laget et førsteutkast av abstraksjoner for kohorter og dokumenter som
;; kan brukes på tvers av enkeltkohorter.
;; Tanken er at disse kan:
;;
;; - redusere boilerplate og duplisering i koden vår
;; - lette inngangsterskelen med å opprette og kjøre i gang nye kohorter
;;
;; Foreløpig har jeg fokusert _kun_ på server-siden.
;; Har ignorert CLI-et.
;; CLI-et har litt andre behov; man ønsker å konfigurere opp en CLI-klient til skriving for én kohort.
;; Eller noe annet.
;; Jeg lar i alle fall denne ligge inntil videre.

;; ## Nye navnerom
;;
;; Følgende navnerom er nye:

(->> [['mikrobloggeriet.cohort "abstraksjon for kohorter"]
      ['mikrobloggeriet.doc "abstraksjon for dokumenter"]
      ['mikrobloggeriet.cohort-test "tester for kohorter"]
      ['mikrobloggeriet.doc-test "tester for dokumenter"]]
     (map (fn [[nssym descr]]
            {"navnerom" nssym
             "formål" descr}))

     (clerk/table))

;; Ta gjerne en titt på enhetstestene.

;; ## funksjoner som tar inn kohort og dokument
;;
;; Mange av funksjonene følger dette mønsteret:

(clerk/code (with-out-str (clojure.repl/source-fn `cohort/docs)))

(clojure.repl/source-fn `cohort/docs)
