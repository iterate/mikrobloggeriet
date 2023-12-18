# VAKT-1: La forskjeller blomstre!

Da jeg startet i Iterate, fikk jeg vite at her lager vi våre egne verktøy.
Da fikk jeg forventninger.
"Åja, flott, så jeg kan faktisk påvirke verktøyene jeg bruker?
Det høres bra ut!"
I praksis var det ikke helt som jeg forventet.
Kodebaser er kodebaser; bare fordi man har kildekoden er det ikke nødvendigvis trivielt å endre.
Jeg har gjort én kodeendring som andre i Iterate bruker minst én gang i måneden: at knappen i timeføringssystemet viser hvilken måned det er man lukker når man lukker måneden.

Kontroll over egne verktøy og kontroll over egen arbeidsflyt har vært et viktig prinsipp og en kilde til inspirasjon for Mikrobloggeriet.
Mikrobloggeriet bør være et verktøy jeg kan være stolt av---et verktøy jeg vil bruke selv.
Derfor er skriveflaten filer i Git du kan endre med din egen editor.
Ikke en shitty web-editor du hater å redigere i!

Men hva med de andre?
Hva med andre sine preferanser?
Hvordan kan andre få den samme gleden i bruken av verktøyene som jeg har fått?

Dette er løsbart!

Kode handler ikke om å sette alt i samme system så alle skal gjøre det samme.
Kode bør organiseres så den er effektiv å jobbe med.
Da bør du kunne velge å følge systemet eller ikke.
Samtidig bør det være plankekjøring dersom man ønsker å følge veien de fleste følger.

Neno lagde denne uka mikrobloggen [URLOG].
Her utfordrer han status quo for mikroblogger på Mikrobloggeriet.
Han vil ikke skrive, han vil lede oss til andre plasser på Internett der vi kan bli overrasket.

Denne typen konsepter har fra dag én vært det jeg ønsker å se, og er grunnen til at vi gjør routing sånn vi gjør.

Her er routing-tabellen:

```clojure
(defroutes app
  (GET "/" req (index req))

  ;; STATIC FILES
  (GET "/health" _req {:status 200 :headers {"Content-Type" "text/plain"} :body "all good!"})
  (GET "/vanilla.css" _req (css-response "vanilla.css"))
  (GET "/mikrobloggeriet.css" _req (css-response "mikrobloggeriet.css"))
  (GET "/reset.css" _req (css-response "reset.css"))
  (GET "/urlog.css" _req (css-response "urlog.css")) ;; NENO STUFF

  ;; THEMES AND FEATURE FLAGGING
  (GET "/set-theme/:theme" req (set-theme req))
  (GET "/set-flag/:flag" req (set-flag req))
  (GET "/theme/:theme" req (theme req))

  ;; STUFF
  (GET "/feed/" _req (rss-feed))
  (GET "/hops-info" req (hops-info req))
  (GET "/random-doc" _req random-doc)

  ;; OLORM
  ;; /o/* URLS are deprecated in favor of /olorm/* URLs
  (GET "/o/" _req (http/permanent-redirect {:target "/olorm/"}))
  (GET "/olorm/" req (cohort-doc-table req store/olorm))

  (GET "/o/:slug/" req (http/permanent-redirect {:target (str "/olorm/" (:slug (:route-params req)) "/")}))
  (GET "/olorm/:slug/" req (doc req store/olorm))

  ;; JALS
  ;; /j/* URLS are deprecated in favor of /jals/* URLs
  (GET "/j/" _req (http/permanent-redirect {:target "/jals/"}))
  (GET "/jals/" req (cohort-doc-table req store/jals))
  (GET "/j/:slug/" req (http/permanent-redirect {:target (str "/jals/" (:slug (:route-params req)) "/")}))
  (GET "/jals/:slug/" req (doc req store/jals))

  ;; OJ
  (GET "/oj/" req (cohort-doc-table req store/oj))
  (GET "/oj/:slug/" req (doc req store/oj))

  ;; GENAI
  (GET "/genai/" req (cohort-doc-table req store/genai))
  (GET "/genai/:slug/" req (doc req store/genai))

  ;; LUKE 
  (GET "/luke/" req (cohort-doc-table req store/luke))
  (GET "/luke/:slug/" req (doc req store/luke)) 

  ;; NENO
  (GET "/urlog/" req (urlog/urlogs req))
  (GET "/urlog/:slug/" req (urlog/doc req store/urlog))

  )
```

Først litt statiske filer, temasystem, RSS-støtte, knapp for å gå til tilfeldig side, la oss kalle det funksjonalitet.
Under er det manuelle oppføringer for hver kohort.
Det gjør at vi har _null implisitt kobling mellom kohorter_.
Og det er lett å støtte gamle URL-er ([Cool URIs don't change]).

[Cool URIs don't change]: https://www.w3.org/Provider/Style/URI

Det var det jeg ville si for nå.
Tusen takk til Neno for at du ikke bare snakker om ideer du har, men gjennomfører.

---Teodor

[URLOG]: /urlog/
