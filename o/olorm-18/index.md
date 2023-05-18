# Go chans & Unix pipes

Jeg har lagt merke til likheten mellom Go channels og Unix (named) pipes.

> Go's approach to concurrency [...] can also be seen as a type-safe generalization of Unix pipes.
>
>—[Effective Go - Share by communicating](https://go.dev/doc/effective_go#concurrency)

Først, hva er named pipes igjen?

```
command | grep foo
```

er en "vanlig" pipe og kan ses på som en spesialisering av en named pipe:

```
mkfifo mypipe
command > mypipe &
<mypipe grep foo
rm mypipe
```

Selv om det opprettes et filnavn, vil ikke data skrives til fila.
Named pipes gir mye mer fleksibilitet for sending og lesing av meldinger enn en navnløs pipeline.

[A Tour of Go - Concurrency](https://go.dev/tour/concurrency/2) introduserer channels med kode som summerer tall med to goroutines.
Sammenlign med min implementasjon i UNIX shell:

```
#!/bin/bash
set -e

sum() { c=$1; shift
    sum=0
    for v in $@; do
        sum=$(( sum + v ))
    done
    echo $sum > $c # send sum to c
}

main() {
    s=(7 2 8 -9 4 0)

    c=/tmp/chan$$ guard=/tmp/pipeguard$$
    mkfifo $c
    mkfifo $guard; >$c <$guard &

    n=$[ ${#s[@]} / 2 ]
    sum $c ${s[@]:0:n} &
    sum $c ${s[@]:n} &
    { read x; read y; } <$c # receive from c

    echo $x $y $((x+y))

    >$guard; wait
}

main
```

Jeg synes koden er veldig lik.
`mkfifo $FILNAVN` tilsvarer `navn := make(chan t)`.

`$guard` opprettes som en (inaktiv) skriver til `$c` for at den ikke skal lukkes for tidlig.
FIFOer (named pipes) lukkes når alle skrivere er lukket (ferdige).

`sum $c ${s[@]:0:n} &` tilsvarer selvsagt `go sum(s[:len(s)/2], c)`.
`&` starter en asynkron prosess, lignende som `go `.
Shell angir som kjent argumenter som ord, uten paranteser og komma.
Slice'en sendes som et slags variadic argument, så det er mest praktisk å ha `c` som første parameter.
(Håndteringen av array i main() er forøvrig eneste Bash-isme i koden.)

Den siste linja i `main()` er bare høfflig opprydding som lukker FIFOene og avventer at alle prosessene er avsluttet.

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

