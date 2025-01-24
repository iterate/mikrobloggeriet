# OLORM-48: Løs kobling

Jeg har [trampet høylytt] og sagt at du er ansvarlig for å bygge delt forståelse av hva som er bra, innenfor din ekspertise.
Jeg er også ansvarlig for å bygge delt forståelse av hva jeg synes er bra.

[trampet høylytt]: /luke/luke-16/

Så hva er god kode?

## God kode er løst koblet

God kode er delt i moduler der hver modul er løst koblet fra andre moduler.

## Men hva er løs kobling?

Jepp, det er det gode spørsmålet.
Jeg vil ikke komme med en formell definisjon av hva løs kobling er; jeg vil heller komme med noen eksempler.

| tettere koblet                                                            | løsere koblet                                                                      |
|---------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| `package.json` har 20 avhengigheter                                       | `package.json` har 16 avhengigheter                                                |
| modul A bruker 6 funksjoner i modul B                                     | modul A bruker 3 funksjoner i modul B                                              |
| modul A bruker modul B, modul B henter tilstand i applikasjonen selv      | module A bruker modul B, all informasjon sendes som eksplisitte funksjonsparametre |
| for å jobbe på en app, kan jeg kjøre kun koden jeg bryr meg om            | for å jobbe på en app må jeg starte 7 ting jeg ikke vet hva er                     |
| enhetstestene krever at databasen kjører                                  | enhetstestene krever ikke at databasen kjører                                      |
| jeg må ut og teste i test- eller prodmiljøer for å finne ut om det funker | jeg kan kjøre det jeg bryr meg om lokalt                                           |

## Liksom-løs kobling som egentlig er tett kobling

Å splitte kode i noe kode en plass og noe kode en annen plass gir oss ikke nødvendigvis løsere kobling.
I verste fall har vi like tett koblet kode, som nå er to plasser.
Da har vi alle problemene vi hadde før (koden er fortsatt tett koblet), men _enda_ et problem: vi må huske på at noen av koden er et annet sted, og vi må vite hva som skjer der for å få gjort noe.

## Det er vanskelig å sikre løs kobling

Da jeg først hørte [James Reeves] si noe ala "developers develop a _smell_ for coupling and learn to avoid it", skjønte jeg ikke hva han mente.
På det tidspunktet hadde folk betalt meg for å skrive kode i nærmere fem år.
Hva betyr det, liksom?
Hva har det å si?

[James Reeves]: https://github.com/weavejester/

I dag ser jeg på kobling som en tung ryggsett.
Hver kobling koden din har til annen kode er en stein i ryggsekken.
Det er tyngre å gå når ryggsekken er full av stein!

## Ekstrem kobling: sykliske avhengigheter

Hvis modul A kaller på modul B, og modul B _også_ kaller tilbake på modul A, er ting så ille som de kan bli!
Du kan nå verken endre A uten å endre B, eller endre B uten å endre A.
Her er to ting du kan gjøre:

1. Slå sammen til én modul.
   Hvis begge modulene bruker den andre, har du ikke fått _fordelene_ med å splitte i moduler.
   Så gå videre med én!
   Kanskje dette _burde_ være én modul.
   Skal du bruke gjensidig rekursjon ("[mutual recursion]"), _må_ begge funksjonene kjenne hverandre (med mindre du bruker en tilstandsmaskin eller noe sånt, men det har ikke jeg hatt behov for å gjøre i praksis _ennå_.)

2. Splitt koden i fire.
   Trekk først ut _grensesnittet_ mellom modul A og modul B.
   Putt grensesnittet i modul G.
   Skriv nå modul A og modul B om til å kun være avhengig av grensesnittet.
   I statiske språk blir dette typer, og/eller "interfaces" (Go, java), "traits" (Rust) eller typeklasser (Haskell).
   Skriv en siste hovedmodul H der du bruker både modul A og modul B.
   Til slutt får du disse avhengighetene:
   
       H -> A
       H -> B
       A -> G
       B -> G
       
   Hovedmodulen (H) heter ofte "main", og vet det den må vite for å starte applikasjonen.
   
[mutual recursion]: https://en.wikipedia.org/wiki/Mutual_recursion

## For å få løsere kobling, må du sannsynligvis gå saktere fram.

Når du har noe som funker, tar du deg tiden til å se på hvordan koden henger sammen?
Burde koden henge sammen på den måten?
Den koden du skriver blir liggende i kodebasen!
Når du nettopp har skrevet koden, kjenner du best hvordan den fungerer.
Senere blir det vanskeligere å splitte opp.

—Teodor
