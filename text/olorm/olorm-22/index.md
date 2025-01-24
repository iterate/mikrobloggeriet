# En fin dag med Elm

I går hadde jeg en veldig fin dag med Elm programmering, og det er ganske lenge siden sist jeg satt meg ned og lagde noe nytt i Elm. Alle nye frontender vi lager har blitt React/TypeScript, først og fremst for å enklere komme tettere på browseren i noen tilfeller og bibliotekstøtte i andre, sekundert er også bekjentskapen til Elm på teamet en faktor.

En Elm modul består av et view, en modell og en update funksjon for å gjøre endringer på modellen. I tillegg har man en init funksjon for å sette opp modellen, ofte med data fra backend. Vi har en rekke forskjellige måter å bestille strikkeplagg på, det kan være uten annen input enn ditt eget hode, et produkt vi har laget i Sanity, en digital strikkeoppskrift eller nå snart, ditt eget design i en 3D-modell. 

For hver av de forskjellige måtene å bestille på henter vi data fra forskjellige steder. I browseren får forskjellige url-er. Modellen og viewet er helt likt i alle tilfeller, men hver url leder til forskjellige init-funksjoner som henter data fra riktig kilde. Det blir veldig oversiktlig og enkelt. For hver init funkson trenger vi også ett innslag i update-funksjonen som setter dataen inn i modellen.

