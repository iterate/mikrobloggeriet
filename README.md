# Mikrobloggeriet

Tidligere kjent som OLORM.

Vocabulary:

| term            | definition                                             | Deprecated? |
|-----------------|--------------------------------------------------------|-------------|
| cohort          | a microblogging collective                             |             |
| doc             | a microblog entry                                      |             |
| mikrobloggeriet | a collection of microblogs                             |             |
| olorm           | a CLI for microblogging                                |             |
| olorm           | a collection of microblogs                             | Deprecated. |
| olorm           | Oddmund, Lars and Richard's microblog collective       |             |
| jals            | Jørgen, Adrian, Lars and Sindre's microblog collective |             |
| jals            | a CLI for microblogging                                |             |

Tragically, the "olorm" term is now ambiguous.
But [Cool URIs don't change].
I don't want to break existing links.

The user experience of OLORM and JALS will suffer a bit from this.
I ask you to to bear with me for the sake of the URIs :)

[Cool URIs don't change]: https://www.w3.org/Provider/Style/URI

## Oppsett før installasjon av kommandolinjeprogram

Skal du bruke et kommandolinjeprogram for å skrive på Mikrobloggeriet?
Da må du først:

1. Klone ned dette repoet
2. Installere [babashka] og [bbin].
   Hvis du bruker macOS, kan du installere disse to med Homebrew.

## Installér kommandolinjeprogram for å skrive OLORM-er

Vennligst:

1. Gå til katalogen der du har klonet dette repoet.

2. Installer programmet `olorm`:

    ```shell
    bbin install ./cli --as olorm --main-opts '["-m" "mikrobloggeriet.olorm-cli"]'
    ```

3. Bruk subkommandoen `olorm set-repo-path` til å peke til der du har klonet repoet.
   Kjør `olorm set-repo-path -h` for å se hjelpetekst for subkommadoen.

## Installér kommandolinjeprogram for å skrive JALS-er [UNDER UTVIKLING]

Per 2023-05-15 14:05 funker ikke dette!
- Teodor

Vennligst:

1. Gå til katalogen der du har klonet dette repoet.

2. Installer programmet `jals`:

    ```shell
    bbin install ./cli --as jals --main-opts '["-m" "mikrobloggeriet.jals-cli"]'
    ```

3. Bruk subkommandoen `jals set-repo-path` til å peke til der du har klonet repoet.
   Kjør `jals set-repo-path -h` for å se hjelpetekst for subkommadoen.

## Install OLORM CLI (deprecated, legacy)

This CLI installation method is deprecated in favor of cohort-specific instructions.
See above.

In order to install `olorm`, please:

1. Clone this repo
2. Install [babashka] and [bbin]
3. Then install `olorm`:

    ```shell
    bbin install ./cli
    ```

You should then be able to invoke the CLI:

    $ olorm -h
    Available subcommands:

      olorm create
      olorm help
      olorm repo-path
      olorm set-repo-path

