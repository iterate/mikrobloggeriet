# UPSERT

I dag hadde jeg behov for å gjøre en UPSERT i databasen min, det vil si å gjøre en INSERT, men hvis det allerede finnes i databasen gjør vi en UPDATE på det som vi har i stedet.

Jeg har en tabell som holder oversikt over hvilken organisasjon en oppskrift tilhører, den er ganske enkel med en `organization_id` og en `pattern_id`.

```
CREATE TABLE organization_patterns (
    organization_id INTEGER NOT NULL,
    pattern_id INTEGER UNIQUE NOT NULL,
    PRIMARY KEY (organization_id, pattern_id)
);
```

Her er `pattern_id` satt som `UNIQUE`, det vil si at den kun kan finnes i en rad i tabellen og jeg kan derfor gjøre en UPDATE hvis jeg prøver å gjøre en INSERT på en `pattern_id` som allerede finnes. Altså at jeg bytter organisasjon til oppskriften i stedet for å legge den til en organisasjon for første gang.

sql-spørringen min for å sette inn/oppdatere blir da:

```
INSERT INTO organization_patterns
    (organization_id, pattern_id)
VALUES (<organization_id>, <pattern_id>)
ON CONFLICT (pattern_id)
DO UPDATE SET organization_id = <organization_id>
```

Til slutt så tenker jeg at kanskje `organization_id` bare skulle vært et eget felt i `pattern` tabellen og alt hadde vært litt enklere? 

