# OLORM-45: `--scale` i Docker Compose

I dag lærte jeg noe kult!

Docker Compose har et `--scale`-argument man kan bruke til å skru av en tjeneste.
Supernyttig når jeg vil kjøre én tjeneste manuelt (feks med `go run` eller `yarn dev`), men vil ha resten i Docker.

Eksempel:

    docker compose up --scale api=0 --scale analytics=0

Så kjører vi `api` og `analytics` manuelt i hver sin terminal:

    (cd api && make run)
    (cd analytics && make run)

Tidligere har jeg kommentert ut og inn tjenester av `docker-compose.yml`.
Det har jeg aldri helt likt.
Det er alltid en sjanse for å comitte sånne endringer og ødelegge for andre.

Kudos til [Christian Duvholt] som skrev Docker Compose-filen som gjorde alt dette mulig.

---Teodor, 2023-11-24

[Christian Duvholt]: https://github.com/duvholt
