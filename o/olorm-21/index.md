# Filtrering av grupper av innslag

På prosjektet har vi en relasjonsdatabase med en ganske stor tabell med over 30 millioner rader.
Innslagene inneholder tidspunkt med "item"s status med mer. Ca slik:

```
+----------+---------+------------+---------+----
| id       | item_id | version_id | state   | ...
+----------+---------+------------+---------+----
| 45649802 |   11111 |          1 | A       |
| 45649811 |   11111 |          2 | B       |
| 45649817 |   12222 |          2 | C       |
```

Vi oppdaget at noen status-innslag manglet, og trengte å hente ut alle disses `item_id`.
Vi ville finne alle som har state `B`, men ikke har hatt state `A`.
Jeg liker `HAVING SUM(CASE` for slike spørringer:

```
SELECT item_id
FROM history
GROUP BY item_id
HAVING SUM(CASE WHEN state = 'A' THEN 1 ELSE 0 END) = 0
   AND SUM(CASE WHEN state = 'B' THEN 1 ELSE 0 END) > 0
```

Men når rekkefølge teller, liker jeg `LEFT JOIN WHERE NULL`:

```
SELECT t1.item_id
FROM history t1
LEFT JOIN history t2
    ON t2.item_id = t1.item_id
    AND t2.version_id > t1.version_id
LEFT JOIN history t0
    ON t0.item_id = t1.item_id
    AND t0.version_id < t1.version_id
    AND t0.state = 'A'
WHERE t1.state = 'B'
    AND t0.item_id IS NULL
    AND t2.item_id IS NULL ;
```

Denne sier to ting:

1. At `B` er siste status (`t2` med nyere rad finnes ikke; `IS NULL`).
2. At ingen tidligere rad med status `A` finnes (`t0`).

Hvis dette feiler (SQL'en tar for lang tid),
kan man velge å frigjøre seg fra databasen:

```
mysqldump -u USER -p \
    --single-transaction --quick --lock-tables=false \
    DATABASE history > hist.sql

grep INSERT hist.sql | tr \( \\n | grep , > hist.csv     

sort -s -t , -k 2 -k 3 hist.csv > sorted.csv
```

De første kommandoene her tar noen minutter.
`sort` bruker lenger tid på store filer, men er veldig robust.

```
< sorted.csv awk -F "'?,'?" '{
    if ($2 != prev) {
        if (match(states, "B$") && !match(states, ",A,"))
            print prev
        states = ""
    }
    states = states "," $4
    prev = $2 }' > itemIDs.txt                                              
```

For hver `item_id` (`$2` = kolonne 2): print hvis siste state var `B` og ingen tidligere state `A` fantes.

## Konkusjon

Det er sikkert mange mulige måter å løse denne oppgaven på, dette var én måte.

For meg virket `LEFT JOIN`-teknikken bra, og jeg synes de deklarative SQL-spørringene er enklere å lese og forstå enn den mer imperative AWK-koden.

Jeg liker dog at man har muligheten til å løse (og dobbeltsjekke) oppgaven på flere måter.

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

