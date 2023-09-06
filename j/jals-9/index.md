# JALS-9

For å gjøre kodeendringer i hovedbranchen i Vake må det lages PR. For disse PR-ene bruker vi Github Action workflows som kjører tester/sjekker på kodebasen som må bli grønne for å kunne merge endringene. Testene omfatter logikk (Python tester), kodeformat, typing, at docker imaget bygger m.m. Kodeendringene i en PR må altså gjennom alle disse stegene for å bli godkjent. 

Grunnen til at vi "enforcer" dette på alle (via PR) er at det holder kodebasen konsistent, mer leselig og forhåpentligvis fri for bugs. Siden det meste går i Python syns vi det er spesielt greit å være litt strenge. For det som går på kodeformat, typing osv. gjør vi tilsvarende sjekker lokalt gjennom pre-commits for å raskere luke ut småting før man pusher ting remote (denne har vi holdt optional, men alle (?) bruker det). 

Ulempen med denne flyten er at det kan ta lengre tid å få inn en PR, og innimellom blir man utålmodig, ventende på sjekker man kanskje vet er helt unødvendig for endringen man prøver å gjøre. For å gjøre dette mer effektivt har vi laget en GA jobb som sjekker hvilke filtyper som er endret i PR-en, og så blir de andre sjekkene utført på bakgrunn av dette. Da slipper vi f.eks. å vente på å sjekke om Docker imaget bygger hvis man kun har gjort en endring i dokumentasjonen.

I sum syns vi det er greit med sjekkene selv om det tar litt tid. Hva tenker dere andre, hvordan gjøres det (eller ikke) i deres team?
