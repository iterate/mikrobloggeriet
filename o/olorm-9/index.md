# OLORM-9

I prosjektet nå bruker vi GitHub Actions til å bygge Docker image av Go-app.

Det er litt kjedelig å endre Go-versjon fra f.eks. 1.19 til 1.20 mange (> 1) steder. 

I `Dockerfile` kan vi enkelt bruke `ARG`/`--build-arg`:

```
ARG GO_VERSION

FROM golang:${GO_VERSION}-alpine
```

I GitHub `.workflow` kjører vi også noen tester med:

```
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-go@v3
        with:
          go-version: 1.20
```

Jeg ønsker å bruke samme versjon begge steder, definert ett sted.

Helt siden [Go 1.12](https://pkg.go.dev/cmd/go@go1.12#hdr-The_go_mod_file) har `go.mod` inneholdt Go-versjonen.

Dette formatet er så enkelt at det ikke er vanskelig å hente ut direkte:

> Each line holds a single directive, made up of a verb followed by arguments.

```
 <go.mod tr -s ' \t\r' ' ' | sed -n '/^ *go [1-9]/s/^ *go //p'
#└┬────┘ └┬──────────────┘   └──┬──┘└─┬──────────┘└────────┬─┘
# │       │                     │     │                    │
# │       │   default ikke print┘     └ for linjer         │
# │       │                             som starter        │
# │       │                             med ' *go [1-9]'   │
# │       │                                                │
# │       │              erstatt ' *go ' med '' og *p*rint ┘
# │       │
# │       └ erstatt alle [ \t\r]+ med ' '
# └ redirect go.mod til stdin
```

Men Go hjelper oss også:

> The `go mod edit` command provides a command-line interface for editing and formatting go.mod files, for use primarily by tools and scripts.

```
go mod edit -json | jq -r .Go
```

Siden denne kommandoen er laget for akkurat dette formålet tenker jeg den er veldig trygg å basere seg på.

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

