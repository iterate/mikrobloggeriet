# Enums i postgres, trenger jeg det egentlig?

Jeg har eksperimentert med å bruke enums i postgres for kolonner som kun kan ha få gitt versjoner, som for eksempel hvilken state eller type noe er. Det er fint for å sørge for at databasen aldri inneholder noen verdier vi ikke forventer i backenden, men å endre på en enum har vist seg å være litt vanskelig. Å endre på enums i postgres ble introdusert i versjon 9.1, men siden jeg gjør databasemigrasjoner med sqlx som kjører i en transaction, får jeg ikke lov til å endre på enums direkte. Jeg må lage en ny og gjøre en rekke operasjoner for å få endre på noe

```
ALTER TYPE subscripton_type_enum RENAME TO subscripton_type_enum_old;

CREATE TYPE subscripton_type_enum AS ENUM ('custom', 'free', 'basic', 'designer', 'professional');

ALTER TABLE design_studio_checkout_sessions
  ALTER COLUMN subscription_type DROP DEFAULT,

  ALTER COLUMN subscription_type TYPE subscripton_type_enum
  USING subscription_type::text::subscripton_type_enum,

  ALTER COLUMN subscription_type SET DEFAULT 'custom';

ALTER TABLE design_studio_subscriptions
  ALTER COLUMN subscription_type DROP DEFAULT,

  ALTER COLUMN subscription_type TYPE subscripton_type_enum
  USING subscription_type::text::subscripton_type_enum,

  ALTER COLUMN subscription_type SET DEFAULT 'custom';


DROP TYPE subscripton_type_enum_old;
```

Siden backend-koden er skrevet i Rust og det naturlig nok kun er den som snakker med databasen, tenker jeg at det kanskje er like greit å holde seg til å kun definere enums i backenden og ikke i databasen i tillegg.
