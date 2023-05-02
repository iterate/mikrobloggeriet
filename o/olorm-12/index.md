# JSON i Postgres

Jeg bruker mye JSON (egentlig JSONB som er mer effektivt) i Postgres og det er både raskt og enkelt å endre, men må brukes med måte.

Når vi laget designverktøyet vårt gikk i utgangspunktet alt av data fra frontenden og rett gjennom backenden som JSON og inn i en tabell i postgres. Det fungerte overraskende bra og gjorde at utviklingen av frontenden ikke trengte å stoppe opp på grunn av backend-oppgaver. Det gjorde frontend (JS/React) utviklerne mer autonome uten at de trengte å lære seg alt av backenden (Rust).

Etterhvert, når man begynner å få litt data som ikke bare kan slettes kan man begynne å savne gode gamle database-skjemaer, spesielt når det gjelder database-migreringer er det godt å kunne lene seg på noen skjemaer.

Som nevnt har vi Rust som backend og den bestemmer hva som får komme i JSON-kolonnene og ikke, det fungerer veldig bra sammen med sqlx som vi bruker til å gjøre spørringer mot databasen. For å skrive JSON til databasen må det ha en egen struct:

```
struct Image {
    name: String,
    alt: Option<String>,
}
```

og spørringen blir derfor

```
let image = Image {name: "123", alt: None };
let yarn_id = 1;

sqlx::query!(r#"
    UPDATE yarn
    SET image = $1
    WHERE id = $2
"#,
sqlx::types::Json(image) as _,
yarn_id
)
.execute(db)
.await?;
```

Her må vi wrappe image i en `Json` type fra sqlx.

For å hente data ut gjør jeg
```
struct YarnQuery {
    image: sqlx::types::Json<Image>
}

sqlx::query_as!(r#"
    SELECT image as "image: sqlx::types::Json<Image>"
    FROM yarn
    WHERE id = $1
"#, yarn_id)
.fetch_one(db)
.await?;

```

Vi ser at sqlx trenger litt hjelp for å vite hvilke type det er når vi setter inn JSON i `as`.


Når det gjelder performance så har postgres støtte for å lage indekser på verdier inne i en JSON-kolonne, uten at vi har hatt bruk for det enda.

