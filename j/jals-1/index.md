# JALS-1

Jeg refaktorerer hvordan vi håndterer trente maskinlæringsmodeller. Vi bruker et verktøy kalt [neptune](https://neptune.ai/) som, etter en trening, lagrer vektene i sin sky. Vektene til en modell representerer det modellen har lært. Når vi skal kjøre _inference_ på ny data (bilder), må vi laste ned disse vektene igjen for å instansiere modellen.

### Problembeskrivelse:

1. Under trening lagres vektene lokalt i en mappestruktur som neptune velger selv, før den lastes opp til sky.
2. Vi ønsker å bruke den samme mappestrukturen når vi laster ned vektene igjen. Følge standarder
3. Når vi skal kjøre inference forholder vi oss kun til en "modell-id", og vet ikke denne mappestrukturen.
4. Vi kan gjøre kall mot neptune for å få denne mappestrukturen.
5. For "viktige" modeller/vekter ønsker vi å cache vektene i vår egen bucket, slik at vi ikke er avhengig av neptune i produksjon.

### Hovedproblem:

#### Hvordan gjenskape mappestrukturen uten å gjøre kall til neptune, og uten å hardkode masse greier?

Dette er et kjedelig problem som jeg ikke finner mye kvalitet i. Jeg tror det er grunnet i en forventning om at verktøyet (neptune) burde håndtere dette annerledes, eller tilby mer fleksibilitet enn det gjør.
