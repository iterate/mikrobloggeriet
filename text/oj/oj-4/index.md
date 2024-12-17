# OJ-4: Hodeløse nettlesere i produksjon

Det finnes noen grunner til at du ønsker å kjøre en _headless_ nettleser i produksjon. Det kan eksempelvis være å ta skjermbilder av en nettside, generere PDFer, skraping, automasjon, eller ende-til-ende tester. Altså om det er noe du _må_ inn i nettleseren for å verifisere eller fikse.
Bibliotek som Playwright, Pupeteer, Selenium og Cypress gjør det høvlig enkelt å skrive kode som interagerer med nettleseren. Og rett slik kan du automatisere hva nå enn du ønsker å gjøre i nettleseren. De vanligste headless nettleserene er Chromiums, Firefox, og Webkit, altså som de vanligste ikke-headless nettleserene, bare headless.

For ordens skyld: en _headless_ nettleser er da en nettleser _uten_ et grafisk brukergrensesnitt, og da ganske egnet for å kjøre i bakgrunnen på din maskin, eller, som jeg skal komme tilbake til, på en server.

Vi jobber med kode som blir vevd inn blant annen kode fra flere andre team, til et og samme produkt. Derfor opplever vi støtt og stadig at endringer utenfor vår kode brekker funksjonalitet vi har ansvar for. Og akkurat derfor fikk vi idéen om å lagen tester som alltid gikk automatisk i bakgrunnen, og sjekket disse tingene for oss. Dette har også medført en del fallgruver, så jeg tenker å vie resten av dette skriveriet til.

## 1. Ikke kjør en nettleser i produksjon om du absolutt ikke må

Stort sett ønsker man å kjøre slike programmer lokalt eller kanskje tester i CI. Et vanlig bruksområde er ende-til-ende tester som trykker seg rundt i applikasjonen din, sjekker at ting er der de skal være, men også at resten av systemet som skal tjene front-enden fungerer. I de aller fleste tilfeller er dette mest relevant å teste ved endringer.
Har du ikke behov utover dette, eller som du tenker det går fint å kjøre fra din maskin er min anbefaling at du holder kjøringene der.

Men så finnes det jo alltid en eller annen grunn til at du kanskje føler du må dette alikevel. Eller en slik grunn greide i alle fall vi å pøske frem for oss selv. Det har jo også gitt litt hodebry.

## 2. Nettlesere krever mye minne og CPU

Appen din bruker nødvendigvis ikke så mye ressurser, men det gjør en nettleser. Det er jo tross alt et ganske stort program man trekker inn.
Derfor må man sørge for at man har gitt tilstrekkelig med ressurser. Og det nettleseren får, er den ofte gira på å ta. For oss har dette innebært mye `OOMKilled` og en del store CPU-spikes, eller throttling om man har limits på slikt. Det krever rett og slett en del skruing på knapper og måling av bruk for å lande på en ideel ressursallokering og skalering. I min erfaring går det ofte fint med litt CPU throttling for _noen_ tester, men blir det for mye endte vi i prakis opp med flaky tester fordi ting lastet for treigt.

Dette gjør også appen, og eventuelt podden veldig mye mer sårbar til å krasje. Ta hensyn for dette, evnt ved å splitte ut egen logikk for orkestrering, og tenke på retry-mekanismer. [Mikroservice-helvette](https://www.youtube.com/watch?v=y8OnoxKotPQ) lusker visst overalt. Mitt beste tips her er å sørge for god tilgang til informasjon om ressursbruk i kjøremiljøet. For Kubernetes har jeg hatt veldig god nytte av [k9s CLI](https://github.com/derailed/k9s) og Grafana.

Og til slutt så er det jo noen som skal betale for skymorroa, og selv om man ikke alltid blir eksponert for sluttsummen selv.

## 3. Bruk grensesnittet ditt bibliotek gir til nettleseren riktig

Jeg tenker jo at dette skulle være åpenbart, helt til jeg skøyt meg selv i foten ved å glemme `browser.close()`. Dette er altså Playwright-funksjonen for å lukke en nettleser, og ved å ikke lukke den mellom hver test endte jeg opp med svært mange nettlesere, og til slutt svært mange OOMKilled og drepte podder. Skriv denne koden med omhu.

Her er det også veldig mange måter å optimalisere, som ikke nødvendigvis er helt intuitiv. Eksemplevis kan dette være parallelisering, eller lage en kø for å kjøre ting sekvensielt.
Dette er avhengig av hva du skal, hvilket bibliotek du bruker, og hvor mye ressurser du har tilgjengelig. Her er det relevant dokumentasjon for ditt bibliotek som gjelder, men for resten har jeg ikke funnet noen tydlig informasjon om hva som er best. Vi kjører ting sekvensielt med en kø.

Her er det også verdt å nevne at nettlesere kan ta inn massevis av argumenter på oppstart, hvor det finnes noen som er ekstra relevant for å kjøre en headless nettleser i containerised miljøer (eksempelvis Docker). Disse varierer fra nettleser til nettleser, og slik jeg tolker det er det viktigere for headless Chromium og headless Firefox, og mindre viktig for headless Webkit. Mange av disse argumentene kommer med avveininger. Eksempelvis ved å skru av sikkerhetsmekanismer som _sandboxing_ for bedre ytelse, eller redusere ytelsen på kost av poteniselt mer flaky tester.

Her er en god oversikt over argumenter til Chromium: https://peter.sh/experiments/chromium-command-line-switches/.

Jeg har ikke funnet en tilsvarende god oversikt for [Firefox config](https://support.mozilla.org/en-US/kb/about-config-editor-firefox) dessverre.

## 4. Nettlesere lager prosesser som ikke nødvendigvis blir ryddet opp skikkelig

Jeg har også fått inntrykk av at nettlesere har en tendens til å starte egne prosesser som ikke nødvendigvis blir avsluttet når man avslutter nettleseren. Dette kalles noe så kult som _zombie processes_! Skrekk og gru! Særlig i containeriserte miljøer er det viktig å huske å drepe disse før de spiser opp PIDer (prossess-IDer for aktive prosesser) og minne. For å drepe zombier anbefales det å bruke et _init_-system i containeren, som [dumb-init](https://github.com/Yelp/dumb-init) eller [tini](https://github.com/krallin/tini). Disse konfigureres da som en parent-prosess til det du tenker å kjøre i containeren, også har innebygd logikk for å rydde opp i nettopp zombie prosesser. I nyere versjoner er forresten _tini_ bygd inn Docker og Podman, men må da startes på `run` med flagget `--init`. Hvordan jeg konfigererer opp args for `docker run` i kubernetes har jeg ikke blitt helt klok på, så jeg ruller med tini for nå.

Selv med dette virker det ikke alltid som nettleser-prosessser blir ryddet opp helt skikkelig, i alle fall mens nettleserne driver å kjører. Jeg har brukt programmer som [htop](https://htop.dev/) til å aktivt monitorere prosessene som lever i min container for å prøve å få bedre bukt på dette. Om noen har noen tips akkurat her tar jeg gledelig i mot.

<br>

Avslutningsvis vil jeg gjerne gjenta at poeng 1. Prøv å unngå å prodsette disse beistene om du kan unngå det. Hodebryet er så mangt, og jeg er ikke i mål. Det finnes også selskaper som tilbyr headless nettlesere som en tjeneste. Det virker veldig digg, men jeg har ikke testet de. Jeg har derimot hentet en del tips og inspirasjon fra ett av de her: https://www.browserless.io/blog/observations-running-headless-browser.

Har du noen tips eller innspill?

-Olav
