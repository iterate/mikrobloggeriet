# Mikrobloggeriet CONTRIB

Her samler vi tips og triks.

## Skriv OLORM i programmet du vil med `EDITOR`

Adrian har brukt `EDITOR` til å velge at dokumenter skal redigeres med [MacDown].

[MacDown]: https://macdown.uranusjr.com/

```shell
# i ~.zshrc, fra Adrian:
function j () {
    EDITOR="open -W -a MacDown" jals create
}
```

`open` på mac tar inn noen argumenter.

- `-W` sier at terminalprosessen skal vente til programmet er avsluttet.
  Det får `olorm`-CLI-et til å vente med `git push` til etter at Adrian har lagret.
- `-a MacDown` ser at det skal åpnes med appen `MacDown`. 

Tilsvarende grep går å gjøre med andre apper.
Eller man kan sette `EDITOR` permanent.

```shell
# i ~.zshrc:
export EDITOR="code -w"
```

Et annet program som respekterer `EDITOR` er Git.
Git prøver å kjøre `EDITOR` til å redigere commit-melding.
`EDITOR="code -w" olorm create` eller `EDITOR="code -w" git commit`.
