(ns user
  (:require
   [clj-reload.core :refer [reload]]
   [mikrobloggeriet.state]))

;; Anbefalt måte å starte opp Mikrobloggeriet er:
;;
;; 1. Kjør `garden run`
;; 2. Koble til REPL fra din editor.

;; Da får du et lokalt miljø som er kliss likt prod. Hvis du alikevel ønsker å
;; kjøre koden herfra, kan du bruke `start!` under.

(defn ^:export start!
  []
  ((requiring-resolve 'mikrobloggeriet.system/start!)
   {}))

(clj-reload.core/init {:dirs ["src" "dev" "test"]
                       :no-unload '#{mikrobloggeriet.state}})

(comment
  (reload)
  mikrobloggeriet.state/datomic
  mikrobloggeriet.state/ring-handler
  )
