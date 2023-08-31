# sed reverse

En mye brukt kommando for å reversere linjene i en fil er følgende:

```
printf 'R\nE\nV\n' | sed -n '1!G;h;$p'
V
E
R
```

Siden sed-kommandoene er veldig konsise kan de fremstå litt kryptiske.
Her gir jeg en forklaring.

sed virker sånn at for hver input-linje evalueres kommandoene.

Ved kjøring kalles linjen under behandling for "pattern space".
Det finnes en tilleggsverdi, kalt "hold space" (i utgangspunktet tom) som man kan lagre til om man vil.

Option `-n` betyr at `sed` ikke skal printe noe output med mindre vi kaller `p`.
Uten `-n` printes pattern space etter hver behandlede linje.

`1!G;h;$p` kan utvides til:

```
1!{  # linjenr != 1
  G  # hent og append hold space til pattern space
}
h    # lagre (pattern space) til hold space (overskriv)
${   # siste linje
  p  # print (pattern space)
}
```

Etter hver kommando ser verdiene slik ut:

linje kommando pattern space hold space kommentar
----- -------- ------------- ---------- -----------
R     1!G      R                        kjører ikke
R     h        R             R
R     $p       R             R          kjører ikke
E     1!G      E\\nR         R
E     h        E\\nR         E\\nR
E     $p       E\\nR         E\\nR      kjører ikke
V     1!G      V\\nE\\nR     E\\nR
V     h        V\\nE\\nR     V\\nE\\nR
V     $p       V\\nE\\nR     V\\nE\\nR  print

Så hver linje blir i praksis prepend'et de forrige, litt som en stack.

Håper sed nå er mindre magisk!

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

