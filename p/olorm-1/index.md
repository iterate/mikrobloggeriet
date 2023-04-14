# OLORM-1

Nå har jeg nettopp _wipet_ rent _worktree_-et mitt med `git reset --hard origin/main`.

Jeg kasta arbeid fordi jeg har lært noe.
Jeg ville endre på noe i et Rust-prosjekt.
Jeg dro den interessante koden ut til en ny funksjon jeg kunne teste.
Jeg lagde en eksempeltest som reproduserte feilen.
Jeg lagde en propertytest som testet at feilen "aldri" ville oppstå.

Så begynte jeg å implementere en løsning.
Men ble bitt litt av forventninger rundt hva jeg kan gjøre med dynamisk formatering av strenger.

Jeg er der i min Rust-reise at jeg stort sett klarer å få ting gjort, men jeg er ikke der at det føles riktig.
Jeg vil ofte løse ting på måter som Rust enten gjør umulig (såvidt jeg vet) eller tungvint.

Jeg spurte Marcus om råd, og han foreslo å lage typer og implementere traits.
Jeg tenkte at det var veldig omstendelig for å implementere den lille greia jeg holdt på med.

Men etter litt demonstrering tror jeg han har rett.

Å lage en type og implementere én trait vil ikke egentlig supplere min funksjon, men erstatte den.
Semantikken i hva jeg prøver på og property testene vil også være lettere å lese og gi mer mening om jeg lager en type til dette.
Og jeg tror det er lettere å finne et naturlig sted for koden å bo om dette er en egen type.

Jeg har lest at det er en vanlig misforståelse å tenke på Rust som et funksjonelt språk.
Og det er nok en feil jeg gjør en del.

Jeg tror jeg vil få mer ut av å lene meg inn i typene til Rust.
Med et lett dryss av objektorientering, kanskje.
