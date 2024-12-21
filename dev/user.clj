(ns user)

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

;; Du kan bruke mikrobloggeriet.repl/state direkte for å få tak i systemet,
;; eller `state` under.

(defn ^:export state []
  (deref (requiring-resolve 'mikrobloggeriet.repl/state)))
