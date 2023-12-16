;; # Pluggable doc transformer

(ns mikrobloggeriet.teodor.pluggable-transformer
  (:require
   [mikrobloggeriet.store :as store]
   [nextjournal.clerk :as clerk]))

;; ## Friksjon i dag

;; Her er kohortene på Mikrobloggeriet:

(keys store/cohorts)

;; Alle disse (per 2023-12-16) er skrevet i Markdown, og publiseres på nett ved å bruke Pandoc til å konvertere Markdon til HTML.

;; For URLOG er dette en dårlig fit.
;; Se på hva Neno må skrive for å få døra si ut i URLOG-1:

(let [cohort store/urlog
      doc (first (store/docs cohort))]
  (clerk/html [:pre (slurp (store/doc-md-path cohort doc))]))

;; Masse distraksjoner!

;; ## Hva kan funke for Neno?

;; Hva om han skrev `url.txt` og puttet én URL i den?
;; Det hadde ikke vært like mye jobb.

(clerk/code "https://www.my90stv.com/")

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
