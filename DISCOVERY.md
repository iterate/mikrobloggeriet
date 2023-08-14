---
title: Discovery log for Mikrobloggeriet
---

# 2023-03-28

## notater

Oddmund, Lars og Richards mikroblogg

1.  Hva gjør du akkurat nå?
2.  Finner du kvalitet i det?
3.  Hvorfor / hvorfor ikke?

Olorm.iterate.no

    Bb tui
    git pul l --rebase
    FZF for å velge builder
    $EDITOR
    Save
    Git commit og push

#olorm på Slack Teodor følger opp manuelt i starten, backup fra Magnus
(?)

## retro

funka det!

-   Var nok jobb i å ta CLI-biten.
-   Og ignorerer støtte for flere input-formater for nå.
-   Mulig IKI-gjenbruk her er ... lite / optimistisk.

# 2023-03-30

## prat med Lars

1.  lars sa han var villig til å prøve en uke
2.  tirsdag eller onsdag etter lunsj etter påske fungerte sannsynligvis
    bra for lars

Ikke hørt med Oddmund og Richard ennå. Håper vi kan finne et tidspunkt
der alle kan være på kontoret.

# 2023-04-16

## Åhh, kjempefornøyd med `olorm.lib`{.verbatim}!!!

Dette synes jeg begynner å bli skikkelig bra.

## Og `clerk`{.verbatim} og `babashka/fs`{.verbatim}!

❤️

## meeeen

Jeg har hardkodet meg inn i at URL til OLORM-er skal være

    /p/$OLORM

ooog nå vil jeg ha

    /o/$OLORM

, selvfølgelig.

### men meen

jeg kan jo bare fikse routeren, og la filene ligge?

hmm.

### Jepp, vi gjør det sånn

# 2023-05-14

## status

1.  jals starter tirsdag
2.  dagens arkitektur er laget for at OLORM er verktøyet, plattformen og
    kohorten
3.  jeg vil ha:
    1.  én plattform (mikrobloggeriet)
    2.  CLI per kohort
    3.  forskjellige kohorter.

Skisse:

-   `mikrobloggeriet.api`{.verbatim} - kjernelogikk.
    -   `client`{.verbatim} -- hvilken mappe, etc.
    -   `document`{.verbatim} -- en olorm, en jals, etc.
-   `mikrobloggeriet.olorm.cli`{.verbatim} - OLORM CLI
    -   bruker `mirkobloggeriet.api`{.verbatim}
-   `mikrobloggeriet.jals.cli`{.verbatim}
    -   bruker =mikrobloggeriet.api

Plan:

1.  Skriv `mikrobloggeriet.api`{.verbatim}
2.  Prøv å migrere over `olorm.api`{.verbatim} til å *bruke*
    `mikrobloggeriet.api`{.verbatim}.
    1.  må hardkode inn en default API-klient for OLORM for å få det til
        å funke.

# 2023-05-15

## status

tror jeg kom fram til noe jeg liker i går. To nye ting:

``` clojure
;; a cohort is a group of people who are writing together

:cohort/name "OLORM"                                  ;; user facing name (unique)
:cohort/ident :olorm                                  ;; identifier (unique)
:cohort/repo-subdir "o"                               ;; where cohort docs are on disk (unique)
:cohort/server-dir "o"                                ;; where cohort docs are on the server (unique)
:cohort/repo-path  "/home/teodorlu/dev/iterate/olorm" ;; where the repo is

;; a doc is a written thing
:doc/number 42       ;; document number, starts at 1
:doc/slug "olorm-42" ;; the documents slug is used on the URL. Globally unique
:doc/cohort :olorm   ;; refers to a cohort ident
```

## goals for today

Want to:

1.  Land new abstraction that enables us to support multiple cohorts in
    a nice way
2.  and must be prepared for the start of the new cohort tomorrow.

## step: document proposed data model in the right place

TODO

## status:

1.  dokumentert ny måte å installere på. README er ish OK.
2.  har fått et nytt modulhierarki jeg har tro på.

mangler:

1.  `mikrobloggeriet.olorm-cli`{.verbatim} peker til den *gamle*
    olorm-implementasjonen.
2.  `mikrobloggeriet.jals-cli`{.verbatim} lager olormer (all koden er
    kopiert)

## interlude

jeg har lyst til å endre navn fra `lib`{.verbatim} til
`domainlogic`{.verbatim}.

## step: olorm peker til rett olorm-backend

1.  [x] `mikrobloggeriet.olorm-cli`{.verbatim} peker til den *gamle*
    olorm-implementasjonen.
    1.  fikset!
2.  [ ] `mikrobloggeriet.jals-cli`{.verbatim} lager olormer (all koden
    er kopiert)

# 2023-07-12

## Hvordan får jeg testet disse greiene?

Det hadde vært fint med CI.

1.  Splitte koden i logikk som kan testes og kjøring av effekter
2.  Skriv tester på logikken
3.  Gå i prod med mindre usikkerhet.

Gjør det lettere å refaktorere, lettere å vite at jeg ikke brekker ting.

Lettere å refaktorere er kanskje hovedfordelen. 🤔

### Eksempel: sjekke hva slags git-kommandoer som har blitt kjørt i forskjellige varianter kommandoer

`--no-git`{.verbatim} og `--no-edit`{.verbatim}

## Nå er koden splittet i logikk og kommandoer! Hva med testing?

Dette var litt gøy.

Så, hva med testing? Nå kan det skrives tester!

Men. Jeg lurer på om jeg har splittet opp litt vel mye. Trenger egentlig
ikke alle disse prosjektene. Kan heller ha:

1.  En krysskompatibel babashka-jvm-dings
2.  Litt JVM for å kjøre selve serveren.

Og det kan jeg splitte i forskjellige mapper:

-   `src/`{.verbatim} - krysskompatibelt
-   `src-serve/`{.verbatim} - JVM-servergreier

Da får jeg slettet en masse mapper.

I dag har jeg flere mapper:

-   `cli/`{.verbatim} - CLI-funksjonalitet
-   `lib/`{.verbatim} - jvm/bb-ting
-   `olorm-cli/`{.verbatim} - ekstra CLI-funksjonalitet som skal slettes
-   `serve/`{.verbatim} - serveren

Så det jeg vurderer er å fjerne alle toppnivå-prosjektene, og heller
jobbe med én bøtte kildekode. Kan eventuelt beholde `serve/`{.verbatim}
inntill videre, og legge til en avhengighet oppover med
`{:local/root ".."}`{.verbatim}.

I samme slengen vil jeg helst endre strukturen til
`mikrobloggeriet`{.verbatim}. `mikrobloggeriet.serve`{.verbatim},
`mikrobloggeriet.cli`{.verbatim},
`mikrobloggeriet.documents`{.verbatim}. Eventuelt
`mikrobloggeriet.api`{.verbatim} i stedet for
`mikrobloggeriet.documents`{.verbatim}.

# 2023-08-04

## Bort med `src-serve`{.verbatim}!

Den ble ikke brukt, gitt.

# 2023-08-13
## Themes

I want theme support.

1. Support changing themes
2. Support using Tailwind to write themes
3. Support using a component library tool (like [Portfolio])

Given theme support, I'd like to invite designers to write and publish their own theme.

[Portfolio]: https://github.com/cjohansen/portfolio


# 2023-08-14

## Hva nå?

_Teodor_

1. Komme i gang.
2. Hypotese: RSS for Olav og AI-bilder for Johan?
   Kanskje først lage et endepunkt for `olorm draw` så vi starter med noe litt lett?
3. Må organisere oppgaver på et vis.
   Tror issues og PR-er er et godt verktøy.


## Hva har vi av issues per 2023-08-14 13:39?

Fra https://github.com/iterate/mikrobloggeriet/issues
:

Ingen.

Fra https://github.com/iterate/olorm/issues
:

- **Problem: ingen sider har <title>** (https://github.com/iterate/olorm/issues/5)
  Løst av Teodor.
- **Ønskeliste** (25. apr) (https://github.com/iterate/olorm/issues/3)
  -  Tittel i html tittel-tag. Hvorfor? Fordi da gir iki vise tittel og forhåndsvisning av innhold i søkeresultater.
     (løst av Teodor)
  -  rss. Hvorfor? Fordi da kan Oddmund lese OLORM med newsboat, eller andre RSS-lesere
  -  olorm draw kan lese historikk selv, så vi slipper POOL-argumentet (olorm draw POOL) 
- **Liste over opsjoner** (5. aug) (https://github.com/iterate/olorm/issues/6)
  -  Støtte RSS
  -  Eksperimentere med stil. (feks forbedre leseopplevelse, sparre med designer, lage støtte for flere temaer)
  -  legge til metrikker for lesing
  -  migrere til ett CLI: bytte ut jals og olorm med ett mblog.
  -  se over google insights-analyse (SEO, tags, ytelse) og se om det er noe vi kan forbedre (https://pagespeed.web.dev/analysis/https-mikrobloggeriet-no/knf5vr3a7p?form_factor=mobile)
