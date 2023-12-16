;; # Pluggable doc transformer

(ns mikrobloggeriet.teodor.pluggable-transformer
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
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

;; 123

(store/docs store/urlog2)

;; funker ikke

(defn doc-exists?
  "True iff doc exists, does not care about content format."
  [cohort doc]
  (and (cohort/root cohort)
       (doc/slug doc)
       (fs/exists? (store/doc-meta-path cohort doc))))

(defn docs [cohort]
  (let [root (fs/file (cohort/repo-path cohort)
                      (cohort/root cohort))]
    (when (fs/directory? root)
      (->> (fs/list-dir (fs/file root))
           (map (comp doc/from-slug fs/file-name))
           (filter (partial doc-exists? cohort))
           (sort-by doc/number)))))

(docs store/urlog2)

;; funker!

;; ## Konklusjoner

;; - Tror det er best å gjøre dette på siden, rett i `mikrobloggeriet.urlog`.
;;   Ikke endre på abstraksjoner før vi vet hva vi driver med.
;; - Vi får det til hvis vi prøver.
;; - Ikke helt åpenbart hvor jorm (templating-biblioteket mitt) passer inn.
;;   Bedre å gjøre det manuelt først tror jeg, _så_ vurdere om jorm kan kutte kobling.

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
