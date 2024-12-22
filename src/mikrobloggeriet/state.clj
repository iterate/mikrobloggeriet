(ns mikrobloggeriet.state
  "Alt Mikrobloggeriet har av tilstand mens vi kjører

  Disse var-ene ligger her så kan få tak i dem. `mikrobloggeriet.state` skal
  IKKE brukes direkte fra andre moduler, unntatt når du sitter i REPL. Kode som
  trenger database eller en referanse til appen, får typisk disse satt sammen
  med http-requesten.")

(defonce ^{:doc "Datomic-database"}
  datomic nil)

(defonce ^{:doc "nextjournal/beholder watchers for new docs. Local dev only!"}
  cohort-watchers nil)

(defonce ^{:doc "Funksjon fra HTTP-request til HTTP-response"}
  injected-app nil)

(defonce ^{:doc "Kjørende HTTP-server"}
  http-server nil)
