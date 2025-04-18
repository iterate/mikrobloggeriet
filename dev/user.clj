(ns user
  (:require
   [clj-reload.core :refer [reload]]
   [clojure.repl.deps :refer [sync-deps]]
   [mikrobloggeriet.state]))

;; Anbefalt måte å starte opp Mikrobloggeriet er:
;;
;; 1. Kjør `garden run`
;; 2. Koble til REPL fra din editor.

;; Da får du et lokalt miljø som er kliss likt prod. Hvis du alikevel ønsker å
;; kjøre koden herfra, kan du bruke `start!` under.

(defn ^:export start!
  []
  ((requiring-resolve 'mikrobloggeriet.system/start!) {}))

(comment
  (reload)
  mikrobloggeriet.state/datomic
  (sync-deps)
  )
