# Å isolere feil når du ikke har tid til å fikse dem

Om du eller noen andre finner en feil oppførsel i et dataprogram du jobber med er det vanlig at første steg videre er å reprodusere feilen.

Er feilrapporten god nok og du har god nok innsikt i koden din, kan det være at du kan reprodusere feilen direkte i en eksempeltest.
Det er et ypperlig utgangspunkt for å fikse feilen.

Men av og til er jobben det vil ta å fikse feilen så stor at du ikke har tid til å gjøre det.
Ellers kan det hende at feilen ikke er grov nok til at du prioriterer å fikse den over andre ting du vil gjøre.

Du vil gjerne ikke la feilende tester ligge å slenge i kodebasen din, så da sletter du den og lager kanskje en GitHub issue eller skriver om feilen på en post-it.

Men om du er kul og lur, beholder du testen din.
I stedet for at den tester at ting går bra, får du den til å teste at feilen produserer en god og unik feilmelding.

I testen kan du kanskje skrive en kommentar om at du ikke prioriterer å fikse feilen her og nå.

Om du bruker Sentry eller noe lignende, kan du nå spore og følge med på feilen. Du kan referere til den og snakke og tenke om den.

Jeg synes dette er bedre enn å ignorere feilen til du får tid/lyst til å fikse den.
