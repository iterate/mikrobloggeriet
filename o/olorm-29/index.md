# Om argument-parsing i Shell

Det finnes noen utbredte konvensjoner som er veldig fint å støtte,
 som at det er lov å gruppere og sortere argumenter etter eget ønske
 ([denne](https://nullprogram.com/blog/2020/08/01/) bloggposten anbefaler jeg):

```
program -i -t -a
program -it -a
program -tai
```

Det er derfor nyttig å bruke hjelpe-kommando eller -bibliotek for å lese argumenter.
 Fish [har](https://fishshell.com/docs/current/cmds/argparse.html) en ganske fin `argparse`,
 som ligner på noe som finnes i mange programmeringsspråk.
 Om Shell ble det sagt:

> argparse. Synes det er skikkelig dritt i sh.

Hvordan kan man gjøre det i shell?
 De ulike shellene - Bash, Zsh, etc. - har sine egne fine løsninger.
 Man kan se på disse spsialtilfellene om man vil.

Nesten alle kommandolinje-konvensjonene ble etablert i tidlige Unix-versjoner,
 på 70- eller 80-tallet.
 I 1986 kom Bourne Shell med en nyere kommando for argumentlesing: `getopts`, og:

> In 1995, `getopts` was included in the Single UNIX Specification [[POSIX](https://pubs.opengroup.org/onlinepubs/9699919799/utilities/getopts.html)] [...] As a result, `getopts` is now available in shells including the Bourne shell, KornShell, Almquist shell, Bash and Zsh.
> — [Wikipedia](https://en.wikipedia.org/wiki/Getopts)

Den er litt annerledes enn `argparse`, men virker godt:

```
all=0
while getopts f:aV opt
do
    case $opt in
        V) version; exit;;
        a) all=1;;
        f) file="$OPTARG";;
        ?) printf "Usage: $0 [-a] [-f file] args\n   or: $0 -V\n";;
    esac
done
```

## Konklusjon

Med `getopts` i en `while`-løkke kan man enkelt deklarere og lese options med (`f:`) og uten (`a`,`V`) _option-argument_.
 Hvis man klarer seg uten `--long-options` er det bare å nyte ☀️  og 🎵.
 Ellers må vi jobbe videre ⛏ 🕳 🐇...

## Tillegg

En utfordring med Bourne Shell er at det ikke har mange datastrukturer.
 Man kan ikke returnere en hash-tabell eller et objekt,
 man må loope over tekstfelter.

Jeg lekte meg med å bygge støtte for `--long-option` og kom opp med:

```
# usage:
# set -- $(parselong a/all f/file -- "$@")
# while getopt f:a...
parselong() {
    c=1
    for arg; do
        [ "$arg" = "--" ] && break
        c=$((c + 1))
    done
    [ $c -eq 1 ] && return

    for arg; do
        case "$arg" in
            ?/*)
                short="${arg%/*}"
                long="${arg#*/}"
                end=
                i=0
                while [ $((i+=1)) -le $# ]; do
                    [ "$1" = "--$long" ] && [ -z "$end" ] \
                        && o="-$short" \
                        || o="$1"
                    [ "$1" = "--" ] && [ $i -gt $c ] && end=1
                    set -- "$@" "$o"
                    shift
                done ;;
            --) break;;
            *) echo "unexpected: $arg"; exit 1;;
        esac
    done
    shift $c
    echo "$@"
}
```

Nå virker `--file foo --all` også!
 Det var ikke trivielt, men _jeg_ synes det var et lærerikt og morsomt dypdykk i shell-argumenter 🙂

Send gjerne spørsmål eller kommentarer til Richard Tingstad

