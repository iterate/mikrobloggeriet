# OJ-3

De siste par dagene har Ole Jacob og jeg utforsket hvordan vi kan kjøre språkmodeller (LLM-er) lokalt på vår maskin. Det vil si, vi fikk et par dager til å leke med denne teknologien for å se om det kunne bidra til å løse et reelt problem i et prosjekt.

Motivasjonen for å kjøre språkmodellene lokalt bunner ut i:

1. Unngå avklaringsrunder og usikkerheter rundt om det er ok å laste opp data til en-eller-annen LLM-leverandør på nett.
2. Slippe å styre med å lage bruker eller skaffe API-nøkkel til en eller flere tilbydere av LLM-tjenester.

I min erfaring er disse begrensningene ofte grunnen til at vi legger språkmodel-eksperiment på is allerede før vi har prøvd.

Her kommer derfor en liten oppsummering av hva vi har gjort med lokale LLMer og hva vi tenker om de.

## Sette opp og kjøre språkmodeller på egen maskin:

Slik satt vi opp vårt språkmodell på vår maskin:

### Kjøre selve språkmodellen

Vi lastet ned [Ollama](https://github.com/ollama/ollama), som er et verktøy for å kjøre LLMer lokalt på maskinen. Man kan tenke litt på Ollama som en runtime for språkmodeller, som kan kjøre og interagere med modellene. Den kommer med et API, som gjør det enkelt for andre programmer å koble seg på.

Ollama kan startes enten ved å laste ned det [offisielle Docker-imaget](https://hub.docker.com/r/ollama/ollama), eller bare kjøre en enkel `brew install ollama` om man vil kjøre den rett på maskina. Vi gjorde sistnevnte.

For å komme i gang lastet vi ned [llama3-8b](https://ollama.com/library/llama3), som er Meta's open-source språkmodell. Med Ollama kan den lastes ned med en enkel `ollama run llama3`

Med `ollama serve` starter Ollama-serveren, som man eksempelvis kan kalle slik:

```
curl -X POST http://localhost:11434/api/generate -d '{
  "model": "llama3",
  "prompt":"Hva er fordelen med å kjøre språkmodeller lokalt?"
 }'
```

Denne spyr den tilbake en response fra din lokale llama3. Her finnes det også biblioteker for å enkelt jobbe med Ollama requester og responser i koden din.

### Sette opp et UI for å interagere med språkmodellen

Det finnes også mange med UI-er man kan klone ned og kjøre lokalt for å få grensesnitt til kjapt interagere med din lokale LLM. Vi valgte å bruke [Dify](https://github.com/langgenius/dify), som gjør det enkelt å behandle filer og bygge workflows LLM-er.
Denne kjørte vi også opp lokalt, men her ved å klone ned repoet og kjøre en `docker compose` etter å ha fulgt [Dify's dokumentasjon](https://docs.dify.ai/getting-started/install-self-hosted/docker-compose). Den starter en del greier, bla. en server, en front-end, en database og en reverse proxy. Her var det litt konfigurering for å sette riktig port i proxyen, og konfigurere den til å kalle endepunktet til Ollama.

Dify har også ganske najs funksjonalitet for å importere data og gjøre det mulig å la en LLM å søke i det. Til dette koblet vi på en annen lokal språkmodel for å håndtere _embedding_.
Videre kunne vi bruke denne dataen, og en språkmodell for å snakke med vår lokale språkmodell. Sånn ser det ut i Dify:

![Skjermbilde fra en veldig enkel pipeline i Dify](https://github.com/iterate/mikrobloggeriet/assets/42978548/5724c2fa-55d1-4cf6-83b0-f422be7d3b32)

Så er det bare å kjøre denne, og chatte med språkmodellen som du ville med en annen GPT.

### Knowledge retrieval fra egen maskin
De fleste språkmodeller er trent på utdatert informasjon, og har videre ikke informasjon spesifikk for din bedrift ol. I vårt tilfelle hadde vi en del filer, som vi ønsket at språkmodellen vår skulle kjenne til innholdet i. Til dette bruker vi teknikker som _embedding_, hvor man kan sende en rekke ulike filer og formater gjennom en annen _embedding_ modell (vi brukte [nomic-embed-text](https://ollama.com/library/nomic-embed-text)). Denne modellen brukte vi til å omgjøre filer til vektorer som kan sendes og tolkes som kontekst av språkmodellen vår. Når vi lagde kontekts kunne vi bla. gjøre mye rart, som å kjøre den på en måte hvor embedding-modellen vår lagde spørsmål-svar kombinasjoner til seg selv. Dette kverna litt på maskina, men tok heller ikke allverdens med tid.

Embedding av tekst er et fundament for å lage det vi ønsket å lage, nemlig en språkmodell som hadde konteksten av våre spesifikke forretningsbehov. Dette er hva som i dag ofte blir omtalt som å Retrieval Agmented Generation ([RAG](https://www.pinecone.io/learn/retrieval-augmented-generation/)). Ideelt sett kan man da bruke også RAG til å søke i egene greier.

### Sidenote om språkmodeller og programmer for å jobbe med disse.
I denne LLM-sfæren virker det som det dukker opp nye modeller og verktøy for å jobbe med de hver dag som går. Det her derfor ikke så mye verdi å bruke for mye tid på hvordan vi har jobbet med _disse_ spesifikke verktøyene på vår maskin. Hensikten er i større grad å vise at de finnes, og hvordan de henger sammen, slik at vi har et bedre grunnlag til å kjapt komme i gang med testing når vi ønser å _"gjøre noe med AI"_.

[Open WebUI](https://docs.openwebui.com/) er et annet alternativ til UI som jeg synes virker spennende.


## Hva ble egentlig resulatet?
Tja. Hittil er det nok ikke superimponerende greier vi har konfigurert opp. Mestparten av tiden gikk med til å finne ut hvor vi skulle starte, og få det til å kjøre ok på vår maskin. Deretter var det å konfigurere data og prompts til å sjekke om denne teknologien faktisk kan løse problemet vårt. Hittil er kanskje ikke resultatene helt _der_, men det har nok noe med datagrunnlaget, promptingen og den litt mer lettkjørt språkmodellen vi brukte. Her tenker jeg en _to be continued..._ er hensiktsmessig.

Jeg så også nå i ettertid at embedding-modellene vi brukte, `nomic-embed-text` stort sett er trent på engelsk tekst, som kan ha bidratt til at den heller ikke leverte varene skikkelig.

## Hva har vi lært?
Det viktigste er at det er ganske rett frem å sette opp en LLM på maskina. Med riktig UI-verktøy kan man også prototype workflowen man ønsker å bruke LLMen i. Vi tenker dette kan gi et godt utgangspunkt til å (1) tørre å eksperimentere med data du ikke føler deg helt komfortable med å sende til himmels, og (2) få oversikt over hvordan du vil koble opp data, LLM og prompts før du begynner å kode det.

Man trenger heller ikke sånn alvorlig mye datakraft for å kjøre de litt enklere LLMene, så å deploye den til en server eller putte den på noe shabby hardware kan fortsatt monne til en del oppgaver. [Vår favorittvideo denne uken](https://www.reddit.com/r/LocalLLaMA/comments/1ceqrsd/lmao_filled_my_poor_junk_droid_to_the_brim_with/) er til inspirasjon, hvor en svenske har fylt svak hardware til bremmen med en usensurert `llama3`-modell, og gjor den litt usikker.

Se opp for en demo fra oss, og ikke nøl med å spør om du er gira på å teste sjæl.



