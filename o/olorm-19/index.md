# Frontend og backend

Det vi lager deler vi ofte opp i en frontend og backend og mye er åpenbart om skal plasseres i frontend eller backend, men noen ting er ikke like rett frem.

Med moderne frontendrammeverk blir det lettere og lettere å skrive mer og mer av logikken til applikasjonen i frontenden, men en del ting vil være mer effektivt å plassere så nærme databasen som mulig. Og det blir kanskje spesielt tydelig etterhvert
som applikasjonen vokser.

En ting jeg ser flere ganger når jeg utvikler endepunkter i backenden vår er at frontend-utvikler i litt for stor grad godtar at det de får av data fra backenden ikke er optimalt. Om man må gjøre først en request og så 5 til, bør man heller ta en runde til med utforming av API-et så man får akkurat det man trenger i frontenden. 

Når jeg utvikler både frontend og backend selv blir det ofte at jeg begynner med backenden, så prøver å løse frontendproblemet og så går noen runder frem og tilbake til jeg finner det optimalet løsningen. Men så er også en av mine svake sider at jeg ikke er så flink til å planlegge godt på forhånd.
