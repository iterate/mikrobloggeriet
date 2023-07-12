# CONTRIB

Her samler vi tips og triks som folk har kommet med (contributions).

- [./mikrobloggeriet.zshrc] - gjør det raskere å skrive jals-er
- [./olorm.el] - lag olorm-er fra Emacs

## Skriv mikroblogginnlegg i programmet du selv ønsker med `EDITOR` og zsh

Mikrobloggeriet respekterer [EDITOR].
Du kan sette EDITOR for å redigere mirkoblogginnlegg med programmet du vil.

[EDITOR]: https://wiki.archlinux.org/title/environment_variables#Default_programs

Du kan sette EDITOR permanent:

```shell
# i ~.zshrc:
export EDITOR="code -w"
```

Eller lage en shell-funksjon som setter EDITOR kun når du kjører funksjonen:

```shell
# i ~.zshrc, fra Adrian:
function j () {
    EDITOR="open -W -a MacDown" jals create
}
```
