# LUKE-4 - Code Coverage

Jeg digger code coverage.

![code coverage example](https://i.imgur.com/lVCM9qB.png)

Code-coverage gir meg mulighet til å se hvilke deler av kodebasen min som har kjørt og ikke har kjørt.
Typisk samler man inn code coverage når man kjører tester. Da får man se hvilke linjer med kode som testen har
"aktivert" og ikke aktivert.

Se på eksempelet over. Dette er litt Rust-kode fra den virtuelle maskinen i Unicad, men hva den gjør er ikke så viktig.
Kode merket med rødt blir aldri kjørt, kode uten farge blir kjørt og i margen kan man se antall ganger koden har blitt kjørt.
Kode merket med blått er en "branch" som har kjørt et annet antall ganger enn den linjen den er en del av.

Her kan vi se at mesteparten av koden blir kjørt i testene, med noen få unntak:

- binary_op feiler aldri i en test, så feilmoden der blir ikke aktivert (spørsmålstegnet er rødt)
- self.negate feiler heller aldri i en test
- Ingen tester får en runtime_error med `VARIABLE is undefined`

Så hva betyr det? Hva gjør vi med den informasjonen?

Først to ord om hva som ikke er nyttig. Code Coverage kan regnes ut som et tall, hvor mange prosent av koden min er dekket av testene.
Og idet man har et tall er det lett å falle for fristelsen til å optimalisere det tallet.
"I denne kodebasen skal vi ha 100% code coverage", eller enda værre "i dette selskapet skal all kode ha code coverage på 100%".
Det er en dårlig idé av mange grunner, en av dem er at det er ganske enkelt å skrive dårlige tester som dekker mye kode uten å være særlig nyttige.

Men det finnes mange gode grunner til å sjekke ut code-coverage.
Jeg tenker på det som et nyttig verktøy når jeg skriver tester, ikke som et tall jeg skal prøve å få så høyt som mulig.

Ofte skriver jeg tester etter jeg har skrevet koden (selv om test-først er et nyttig verktøy er det ikke noe som alltid fungerer).
Da er det veldig nyttig å se på code-coverage før og etter jeg har skrevet en test.
Hvilke del av koden var rød før, men blir kjørt nå.
Stemmer det med det jeg trodde skulle skje?
Om ikke, så betyr det kanskje at min mentale modell ikke stemmer med koden.
Det er en gyllen anledning til å oppdatere min mentale modell til å bli bedre.

Skal jeg gjøre en refaktorering er det ofte nyttig å starte med å se på hvilke deler av den gamle koden som ikke er testet, og prøve å skrive tester for den.

Har jeg en bit kode som jeg tenker jeg har testet ut alle varianter av, titter jeg gjerne på code coverage.
Kanskje får jeg meg noen overraskelser.
Om jeg ser noe kode som er rødt, hva betyr det?
Er det feil i testene, mangler jeg kanskje en test?
Eller er det feil i koden?
Kanskje jeg har en kodebit som ikke trengs?

Hvis jeg trenger en pause en dag, og har lyst til å gjøre noe annet kan jeg bruke litt tid på å se over code-coverage-rapporten for kodebasen jeg jobber i.
Se om jeg finner noe kode som jeg mener burde vært testet, men ikke er det.
Prøver å lage en test som gjør at koden kjører.
Sjekker ut code-coverage på nytt, endret det seg?

Om jeg ikke klarer å lage en test som kjører koden, kanskje koden ikke trengs?

Hvilken kode mener du er viktigst å teste?

Er det noe kode som ikke er så viktig å teste?
