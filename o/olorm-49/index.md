# Send signaler til kommando

Jeg kjører for tiden en kommando som tar lang tid
og inneholder en `sleep` for å ikke overvelde systemer den kaller:

```
cat big | while read in
  do
    echo $in
    sleep 1
  done | process
```

Ulempen med statiske, langlevde kommandoer er 
at endringer krever omstart.

Jeg ville justere `sleep 1` under kjøring,
og en måte å gjøre dette på er å bruke _signaler_:

```
cat big | (
  trap 'i=$((i+1))' USR1
  trap 'i=$((i-1))' USR2
  sh -c 'echo Signal $PPID' >&2
  i=1
  while read in
  do
    echo $in
    sleep $i
  done) | process
```

(Visste du at du kan blande imperativ kode
inn i en pipeline sånn?)

Hvis denne skriver ut `Signal 139` kan jeg gjøre:

```
kill -s USR1 139  # inkrementer $i
kill -s USR2 139  # dekrementer $i
```


—Richard Tingstad

P.S. Inspirert av (GNU) `xargs`.

