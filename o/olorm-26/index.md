# OLORM-26: Unix på godt og vondt

Da jeg skrev første versjon av `olorm`-kommandolinjeprogrammet, ble jeg superengasjert da jeg fant ut at jeg kunne bruke miljøvariabelen `EDITOR`.
`git` bruker `EDITOR` til å la brukeren styre hvilken program brukeren ønsker å redigere Git-commit-meldinger med.
Hvis du vil skrive commit-melding med Vim, kan du kjøre `EDITOR=vim git commit`.
`EDITOR=emacsclient -nt` gir deg en Emacs-instans i terminalen, og `EDITOR=code -w` gir deg Visual Studio Code.

Adrian skrev til og med et alias så han fikk det som han ville.
Nå finner jeg det ikke, men jeg tror han gjorde noe sånt:

```bash
# i ~/.zshrc
j() {
    EDITOR="open -a markdownedit -w" olorm create
}
```

Så Adrian kunne få til det han ville helt på egen hånd!
Det synes jeg er dritkult, vi får i tillegg løftet opp hva Unix/Posix egentlig er.

På den andre siden:

1. Vi har støtt på uforutsette problemer /hver gang/ nye personer har installert CLI-et.
2. CLI-et har tilgang til mappesystemet, som gjør at vi åpner for at folk som skriver OLORM og JALS kan bli utsatt hvis jeg (Teodor) blir utsatt for angrep via avhengigheter.
3. Mange folk bruker ikke terminalen.
   Det hadde vært dritgøy å få designere, produktledere og andre til å skrive.

Men!
Jeg mener fremdeles vi har tatt gode valg.

1. Vi har shippet
2. Og vi har gjort det på en måte som har fått de som har skrevet til å fortsette å skrive.

"Funker ikke perfekt for alle helt ennå" kan vi løse i fremtiden, hvis vi velger å fokusere på det.
