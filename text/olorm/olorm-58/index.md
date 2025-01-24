# Tidlig verdi, høy kohesjon og lav kobling: tre lekser fra Mikrobloggeriet

Morn :)
Teodor her.
Å jobbe med Mikrobloggeriet har vært en læringsreise for meg.
Fra starten ønsket jeg å lage en arena der vi kan lære sammen og være nysgjerrige; der selve systemet også er formbart og inkluderende.
Per 2024-12-21 teller Mikrobloggeriet 1669 commits, og om lag 100 publiserte mikroblogginnlegg (som får meg til å tenke at en statistikkside hadde vært gøy).

Snøen faller i Oslo, og kanskje roen vil senke seg etter hvert.
Å roe ned for meg handler ofte om refleksjon og langsiktighet.
Når stresset letter litt og antallet ting som skal gjøres hver dag faller, kommer motivasjonen til å se litt tilbake og å se litt fram snikende.

Jeg vil gå gjennom tre ting jeg mener har vært viktig, hvorfor det har vært viktig, og hvorfor det som var viktig ikke var intuitivt i første omgang.

## Tidlig verdi

Målet mitt med Mikrobloggeriet var ikke å ha en kodebase jeg kunne pelle på, det var å skrive sammen om ting vi hadde lært.
Jeg definerte verdi som "vi som skriver på Mikrobloggeriet skal få noe ut av å skrive sammen".

Første milepæl var å få en start på innholdet.
Her er committen der Lars Barlindhaug skrev OLORM-1:

[https://github.com/iterate/mikrobloggeriet/commit/b42b365f00e1ad7630f21cd43186804aee8e392a](https://github.com/iterate/mikrobloggeriet/commit/b42b365f00e1ad7630f21cd43186804aee8e392a)

Og her er hele innholdet:

```markdown
# En god natts søvn

I går holdt jeg på en litt kompleks refaktorering hvor jeg skal hente
fargevarianter for en garnpakke fra to forskjellige steder i databasen, avhengig
av om de er opprettet av designeren av oppskriften eller strikkeren har laget
sin egen fargevariant. Det endte med at jeg ikke ble ferdig før jeg måtte gå
hjem, og jeg så for meg at jeg trengte å bruke et par timer på det i dag. Hadde
jeg blitt på jobb hadde jeg nok også lett brukt et par timer.

Når jeg kom på jobb i dag så jeg umiddelbart hva som var feil, et lite stykke
unna der jeg jobbet i går, og fikset alt på ca 5 minutter.
```

På det tispunktet _fantes ikke nettsiden_.
Poenget var ikke å lage nettside, poenget var å skrive sammen og dele hva v i har skrevet.

Lekser:

- Tidlig produktverdi er ofte fullstendig frakoblet hva man har skrevet av kode.
  Målet med Mikrobloggeriet var å skrive sammen for å dele, lære og utforske, ikke lage webapp.
  Teknologien for å publisere tekst på nett er velprøvd, vi har hatt HTML i 30 år.
  Usikkerheten rundt om folk kom til å prøve å skrive én gang, og deretter om folk kom til å fortsette å skrive var betydelig større.

- Hva som gir mening fra et produktperspektiv og hva som gir mening fra et utviklerperspektiv er her helt forskjellige ting, og begge deler må funke.
  Hvis du skal ta på deg både produkt og utviklerhatten, må du både definere et verdiforslag du kan ta ut i verden ("product discovery") og faktisk ta verdiforslaget ut i verden ("product delivery").

- Ting som lever i verden og brukes av ekte folk er gjerne enda mer spennende å putle med enn stæsj man koder litt på på egen maskin!
  Når ekte folk bruker verktøyet du har laget, blir alt mer ekte.

## Høy kohesjon

Holdningen min til arkitektur i Mikrobloggeriet var lenge at ett navnerom holder.
Jeg vil dra fram ett blindspor, og en suksess.
Vi starter med blindsporet.

Helt i starten splittet jeg opp Mikrobloggeriet-koden i to mapper: CLI-et for å skrive innlegg, og HTTP-serveren som skal vise Mikrobloggieriet på Internett.
Det lagde flere problemer enn fordeler.

- Editorer som Emacs og VSCode gir mer hjelp når man redigerer én mappe med kode
- Når man har én mappe med kode har man ett sett med avhengigheter
- Når man har én mappe med kode, er å installere avhengighetene til den ene kodebasen noe man gjør én gang
- Når man har én mappe med kode, er bruken av koden frakoblet fra koden.
  Man står fritt til å organisere koden der man vil logisk i strukturen, uten å tenke på hvilket underprosjekt som har hvilken funksjonalitet.

Mikrobloggeriet er nå én mappe med én kodebase, og det har fungert mye bedre for meg.
Kutt kompleksitet, og bli glad! 😊

I selve server-koden gjorde jeg et valg jeg i dag er mer fornøyd med: putt server-ting i `mikrobloggeriet.serve` inntill ting har et bedre sted å være.
Serveren startet som en router og noen HTTP-handlere.
Routeren sier hvor en gitt HTTP-request skal, og HTTP-handlere tar inn en HTTP-request og gir en HTTP-respons.
Jeg tror både Johan og Olav har bemerket at `mikrobloggeriet.serve` er stor!
Og det er jeg helt enig i.
Store moduler som ikke er "ferdig splittet" er litt vanskelige å sette seg inn i.
Det går liksom ikke å "skjønne hele greia" på ett forsøk, man må heller se på hvordan koden brukes i enkelte konkrete tilfeller.
Starte med å skjønne hva som skjer på en `GET /`, gå videre derfra.

Lekser:

- Ved å splitte opp for tidlig risikerer du å splitte opp på feil måte, som kan gjøre enda mer vondt enn å ha ting samlet en stund.
  Ekstremvarianten er tidlig, voldsom splitting i separate tjenester, som Olav advarer mot i [OJ-4]:

    > Dette gjør også appen, og eventuelt podden veldig mye mer sårbar til å
    > krasje. Ta hensyn for dette, evnt ved å splitte ut egen logikk for
    > orkestrering, og tenke på retry-mekanismer. Mikroservice-helvette lusker
    > visst overalt. Mitt beste tips her er å sørge for god tilgang til
    > informasjon om ressursbruk i kjøremiljøet.

- Store moduler kan gjøre vondt når man ikke kjenner koden.

[OJ-4]: /oj/oj-4/

## Lav kobling

Så, hva gjør man når modulen har blitt for stor?
At modulen er for stor betyr gjerne at den gjør for mange ting.
Hvis vi klarer å trekke noe til side, er det bra!

MEN.

Hvis det faktisk skal bli enklere, må det vi trekker til side være lavt koblet med resten av koden.
Ellers blir det ikke enklere å jobbe med!

Her kan du vurdere om det funker ved å lese koden direkte.

1. Leser koden i hovedmodulen bedre etter at du trakk til side et lite problem?
2. Hvor mange ganger kaller du fra hovedmodulen til sidemodulen?

Hvis hovedmodulen faktisk leser bedre etter at du har splittet opp, og det ikke er alt for mange funksjonskall fra hovedmodulen til sidemodulen, har du lav kobling!

## God jul :)

Håper julefreden senker seg hos deg også!
Hilsen Teodor

☃❄🎄

## Les, skriv, og lær! Det er lov å være nysgjerrig!

Ikke sant, jeg må huske å stille spørsmål til publikum!

Jeg oppfordrer til det sterkeste til å skrive på Mikrobloggeriet, både om ting du lærer (innlegg) og kode.
For meg har det vært superviktig, gitt meg et lass av nyttige erfaringer, samt mye mer mot bak meningene mine om produkt og teknologi.

Hvis du synes _dette innlegget_ var spennende, oppfordrer jeg til å skrive et eget innlegg!
[ITERATE-kohorten] er for eksempel et fint sted å skrive hvis du jobber i Iterate.

[ITERATE-kohorten]: /iterate/
