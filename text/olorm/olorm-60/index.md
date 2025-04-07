# Den omfangsorienterte designeren

Jeg digger å jobbe med designere som aktivt tar stilling til omfanget av jobben som skal gjøres. Les hvorfor.

## Kontinuerlig leveranse

Designskissen skinner så du ser refleksjonen i den. Alt er perfekt. Fargene, forholdene. Teksten ser ut som en dessert fra en michelinstjernerestaurant. Du har et problem.

Hvor i all verden skal du starte?

Ifølge DORA ([dora.dev/capabilities/continuous-delivery]) leverer produktteam
som praktiserer kontinuerlig leveranse jevnt over bedre produkter. Når man skal
levere kontinuerlig må én bit leveres om gangen; og bitene som leveres
informerer videre utvikling.

[dora.dev/capabilities/continuous-delivery]: https://dora.dev/capabilities/continuous-delivery/

Men hvor i all verden skal du starte?

## Den pixel-perfekte prototypen

Å finne det gode stedet å starte er en designjobb.
Men det designet treffer ikke sluttbrukeren: det designet treffer utvikleren og designeren.
En piksel-perfekt prototype peker ikke på hvor det er best å starte.
Den gjør det motsatte: den sidestiller alle tingene som kan gjøres.

I stedet, finn ut hva første versjon av produktet _skal gjøre_.
Hvem skal oppnå hva?

Hvis du ikke stiller det spørsmålet og svarer på det med en hypotese, har du ikke et tydelig mål.
Da smetter "det skal se ut som på skissen" inn som mål i stedet.
Da får du:

- Ukjent/uklar produktverdi
- Ukjent/uklar ferdigstillingsdefinisjon
- Masse work in progress.

Jeg mener vi bør unngå å ta opp work in progress når vi kan, se for eksempel [OLORM-50: For mye opsjonalitet].
Når målet med arbeidet i tillegg er udefinert, har vi lagd flere problemer for oss selv enn vi har løst.

[OLORM-50: For mye opsjonalitet]: /#olorm-50

## Den omfangsorienterte designeren

Nivå 1 av jobben til den omfangsorienterte designeren er å ta "vi skal definere et smalt neste steg" seriøst. Ikke gjør mer enn nødvendig på én gang. Tenk "hvordan skal det tas i bruk" fra steg 1. Da lager vi så lite work in progress vi får til; vi setter oss i posisjon hvor vi kan fullføre.

Nivå 2 av jobben til den omfangsorienterte designeren er å kode det opp direkte i HTML og CSS. Lag noen HTML-filer og CSS-filer i kodebasen. Det blir da ettertrykkelig tydelig hvordan man kan blåse liv i prototypen med ekte data og ekte interaksjoner.

Med noen runder HTML og CSS i koden kommer utvikleren fort til å klø seg i hodet
  eller skjegget (eller andre steder med kroppshår) og tenke "hvorfor kan ikke
  designeren bare endre produktet direkte?"
Det er et veldig godt spørsmål!
Og svaret er _ja_. Det går.
Men da må du rigge kodebasen så designeren faktisk får gjort jobben sin når desineren setter seg ned for å jobbe.
Det betyr at designeren får fokusert på det visuelle utrykket, uten å bli distrahert av detaljer som ikke har med visuelt inntrykk å gjøre.

## Visuelt arbeid på Mikrobloggeriet: ujålete CSS-filer, punktum.

Redesignet Mikrobloggeriet fikk vinteren 2025 er skrevet rett i CSS
av Neno, som er utdannet designer.
Neno kjører kodebasen lokalt, endrer CSS-filer og pusher og prodsetter
med Git.
Vi har gjort "push og prodsett" til én kommando.
Hvis den var et bash-script, ville den sett cirka sånn ut:

```shell
set -e # ellers stopper ikke Bash når noe tryner, lol
./test.sh
./lint.sh 
./try-deploy.sh # deploy feiler hvis appen ikke klarer å starte opp 
git push
```

"men er ikke gå rett i prod skummelt, da?"
I praksis, nei.
Det er tryggere.
Det er lettere å forbedre systemet, inkludert å legge på nødvendig sikkerhetsnett.

## Den omfangsorienterte designeren lar produktteamet levere raskere og bedre

Design-utvikler-interaksjonen i Mikrobloggeriet-arbeid er såpass bra at jeg vil
jobbe hardt for å beholde den flyten. Som utvikler får jeg fokusert på
domeneproblemer og tilgjengeliggjøring av data, og slipper "implementer denne
plx"-gjørejobber. Det gjør at vi klarer å shippe redesign på rekordtid på et
hobbyprosjekt vi gjør ved siden av vanlig jobb.

Hvis du er designer eller utvikler, anbefaler jeg å prøve det. Gøy og bra :)

Takk til Neno Mindjek og Eirik Backer for gode diskusjoner om samspillet mellom
utviklere og designere, og til Christian Johansen og Magnar Sveen for gode
tanker om hvordan framside-kode kan struktureres på enkelt vis.
