# GitHub knakk byggene våre

I går feilet plutselig flere av våre CI-bygg uten tydelig årsak.

Etter tur innom noen blindgater der vår kode var endret — men tilsynelatende ikke relevant — fant jeg til slutt årsaken
etter kjøring av flere modifiserte test-bygg for feilsøking.

Vi bruker i mange GitHub Workflows:

```
    runs-on: ubuntu-latest
```

(Eller `ubuntu-22.04` eller `ubuntu-20.04`, det samme gjelder.)

Disse runner-image'ene fikk nylig en oppgradering: [Ubuntu 22.04 (20230903) Image Update](https://github.com/actions/runner-images/releases/tag/ubuntu22%2F20230903.1), [20.04 (20230903) Image Update](https://github.com/actions/runner-images/releases/tag/ubuntu20%2F20230903.1), der de endret blant annet:

Category Tool name  Previous (20230821.1.0) Current (20230903.1.0)
-------- ---------- ----------------------- ----------------------
Tools    Compose v2  2.20.3                   2.21.0

[Docker Compose release notes 2.21.0](https://docs.docker.com/compose/release-notes/#2210) sier at:

> The format of `docker compose ps` and `docker compose ps --format=json` changed to better align with docker ps output.

Sistnevnte kommando returnerte tidligere en json-array, men returnerer nå linjeseparerte json-objekter.
Dette er en breaking change (ikke-bakoverkompatibel endring).

Jeg synes dette er en litt kjedelig situasjon.
At Docker Compose brekker APIet sitt så lemfeldig.
At vi har ikke reproduserbare bygg fordi bakken endrer seg under føttene våre.
At dette er normalsituasjonen?

Hva tenker du?

—Richard Tingstad

