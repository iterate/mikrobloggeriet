# Den omfangsorienterte designeren

Jeg digger å jobbe med designere som aktivt tar stilling til omfanget av jobben som skal gjøres. Les hvorfor.

## Kontinuerlig leveranse

Designskissen skinner så du ser refleksjonen i den. Alt er perfekt. Fargene, forholdene. Teksten ser ut som en dessert fra en michelinstjernerestaurant. Du har et problem.

Hvor i all verden skal du starte?

Ifølge DORA leverer produktteam som praktiserer kontinuerlig leveranse jevnt over bedre produkter. Når man skal levere kontinuerlig må én bit leveres om gangen; og bitene som leveres informerer videre utvikling.

Men hvor i all verden skal du starte?

## Den pixel-perfekte prototypen

Å finne det gode stedet å starte er en designjobb. Men konsumentene av designet er utvikleren og designeren. En piksel-perfekt prototype er verken en løsning på hvor det er best  å starte, eller rett medium. Finn ut av hva produktet skal kunne gjøre.

Alternativet er å lage en haug med jobb hvor suksess er "implementert skissen" ikke "løser $KONKRET_PROBLEM for brukeren". Hva oppnår man da?

Ukjent/uklar produktverdi

Ukjent/uklar ferdigstillingsdefinisjon

Masse work in progress.

Work in progress bør unngås, selv om vi trenger å ta opp litt nå og da. Når "done" i tillegg er udefinert, har vi lagd flere problemer for hverandre enn vi har hjulpet til.

## Den omfangsorienterte designeren

Nivå 1 av designjobben er å ta "vi skal definere et smalt neste steg" seriøst. Ikke gjør mer enn nødvendig på én gang. Tenk "hvordan skal det tas i bruk" fra steg 1. Da lager vi så lite work in progress vi får til; vi setter oss i posisjon hvor vi kan fullføre.

Nivå 2 av designjobben er å kode det opp direkte i HTML og CSS. Lag noen HTML-filer og CSS-filer i kodebasen. Det blir da ettertrykkelig tydelig hvordan man kan blåse liv i prototypen med ekte data og ekte interaksjoner.

Med noen runder HTML og CSS i koden kommer utvikleren fort til å klø seg i hodet eller skjegget (eller andre steder med kroppshår) og tenke "hvorfor kan ikke designeren bare endre produktet, da?" Og det er mulig! Men det krever at du rigger til kodebasen så designeren kan få se effekten av visuelle endringer uten å skjønne kode-detaljer som ikke har noe med visuelt arbeid å gjøre.

## Visuelt arbeid på Mikrobloggeriet: ujålete CSS-filer, punktum.

Redesignet Mikrobloggeriet fikk vinteren 2025 er stort sett skrevet rett i CSS av Neno. Neno kjører kodebasen lokalt, endrer CSS-filer og pusher og prodsetter med Git. "push og prod" er én kommando. Hvis det var et bash-script, kunne det sett sånn ut:

set -e # ellers stopper ikke Bash når noe tryner, lol ./test.sh ./lint.sh ./try-deploy.sh # deploy feiler hvis appen ikke klarer å starte opp git push

Det har så lang, såvidt meg (Teodor) kjent fanget alle feil relatert til designjobbing.

## Den omfangsorienterte designeren lar produktteamet levere raskere og bedre

Design-utvikler-interaksjonen i Mikrobloggeriet-arbeid er såpass bra at jeg vil jobbe hardt for å beholde den flyten. Som utvikler får jeg fokusert på domeneproblemer og tilgjengeliggjøring av data, og slipper "implementer denne plx"-gjørejobber. Det gjør at vi klarer å shippe redesign på rekordtid på et hobbyprosjekt vi gjør ved siden av vanlig jobb.

Hvis du er designer eller utvikler, anbefaler jeg å prøve det. Gøy og bra :)

Takk til Neno Mindjek og Eirik Backer for gode diskusjoner om samspillet mellom utviklere og designere, og til Christian Johansen og Magnar Sveen for gode tanker om hvordan framside-kode kan struktureres på enkelt vis.
