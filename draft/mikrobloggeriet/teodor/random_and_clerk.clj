(ns mikrobloggeriet.teodor.random-and-clerk)

;; Olav: vi snakket såvidt om caching i Clerk.
;; nextjournal.clerk/clear-cache! sletter _hele_ cachen.
;;
;; Hvis du i stedet setter `:nextjournal.clerk/no-cache` til `true` i
;; metadataen (foran et toppnivå-uttrykk) vil Clerk alltid regne ut uttrykket.
;;
;; Eksempel:

^:nextjournal.clerk/no-cache
(rand)

;; Dokumentasjon for `:nextjournal.clerk/no-cache:`
;; https://book.clerk.vision/#cached-evaluation
