# JALS-7

Jeg jobber nå med refactorering (igjen). Målet er å fjerne kompleksitet og øke fleksibilitet i en flyt. Denne flyten kan beskrives som: hvordan lese et satellittbilde for:

1. trening (ML)
2. prediksjon i prod (inference)

Bildene er veldig store, og passer ikke i minnet ("CPU-" eller GPU-minne), så vi chunker opp bildet i mindre vinduer. Kan sammenlignes med convolution, "bare ett lag ut".

![strided-concolution](https://upload.wikimedia.org/wikipedia/commons/0/04/Convolution_arithmetic_-_Padding_strides.gif)

Se for dere at proposjonene er 10.000 x 10.000 for satellittbilde (blå rute) og 512 X 512 for hver chunk (vindu vi laster inn i minnet om gangen). Som i gif'en vil vi også la hver chunk overlappe med den forrige med feks. 50 pixler.

Jeg fant et relativt nytt bibliotek [TorchGeo](https://pytorch.org/blog/geospatial-deep-learning-with-torchgeo/) som kan erstatte mye av den kompliserte koden. Denne har vi en del krav til for at den skal kunne støtte vår bruk.

1. Den må ikke være tregere enn den eksisterende løsningen
2. Den må ikke stille strenge krav til oss på feks formater eller fil-struktur
3. Det må være enkelt å tweake den
4. ... jeg kommer på fler og fler

Jeg gjorde noe de-risking ved å én-til-én teste med den eksisterende koden og fant fort ut at den kom til kort på ett punkt. Den kan ikke lese bilder direkte fra en cloud bucket, slik som vår kode kan.

Her har heller ikke kildekoden lagt opp til at man kan tweake den så lett. Jeg må override hele `init`-metoden kun for å fjerne en sjekk på om filen eksisterer. Selve lese-metoden klarer fint å lese fra bucket, men sjekken gjør ikke.

Da har jeg to alternativer:

1. Overskrive `init`
2. Lage en PR til biblioteket

I nummer 1. vil jo min implementasjon overskrive resten av logikken i `init` for alltid, og fremtidige oppdateringer i kildekoden går tapt.

Det riktige er helt sikkert nummer 2. med en fiks som løser problemet for alle andre brukere av bibliotek. Men det tar tid, og jeg må gjøre det skikkelig og sette meg inn i nye ting jeg ikke kan. Samtidig så har jeg jo en stor gjeld til open-source-miljøet. Samtidig har jeg ikke fått de-risket biblioteket fullt ut enda. Jeg tror jeg må gå for 2 likevel, må jeg ikke?