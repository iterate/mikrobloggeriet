# LUKE-9

I en tid før grafiske brukergrensesnitt
pleide IT-folk å sende slike julehilsener til hverandre:

```
     ◆                                     ◆
             ⎽⎽⎽⎽⎽⎽⎽⎽┌┐⎽⎽                 / \
 ↓          /        └┘  \               / → \
            ┬────────────┬              / °  °\
────────────┤ ┼┼ ┌──┐ ┼┼ ├─────────────/ ↓~ °  \
            │    │-.│    │            /⎽⎼─┬─┬─⎼⎽\
            └────┴──┴────┘              ┌─┴─┴─┐
                                        └─────┘
```

Denne kommer fra et [arkiv][] av "VT100-animasjoner",
mange stammer fra 1980-tallet.

Disse er jo hyggelige og fine, men... 🤔
ASCII-standarden fra 1963+ inneholder bare 128 symboler
og `┐`, `┴` og `◆` er ikke blant dem —
Unicode og UTF ble jo ikke oppfunnet før på 1990-tallet!
Jeg leste filene direkte, så
hvordan virker dette egentlig?

VT100 er en dataterminal som ble utgitt i 1978
(fysisk maskin, produsert av Digital Equipment Corporation, DEC)
og manualen sier:

> The VT100 has many control commands which cause it to
take action other than displaying a character on the
screen. In this way, the host can command the terminal to
move the cursor, change modes, ring the bell, etc.
> [...]

-----------------------------------------
Character set               G0 designator
--------------------------- -------------
United States (USASCII)     ESC ( B

Special graphics characters ESC ( 0
 and line drawing set
-----------------------------------------

Så hvis terminalen mottar tegnene `ESC` `(` `0`
bytter den tegnsett til Special graphics and line drawing.

Mange av disse kontrollkodene er standardisert i
ANSI X3.64 og X3.41/[ECMA-35][](1980),
men _hvilke_ tegnsett (`B`, `0`) som implementeres er ikke spesifisert.

Uansett kan vi lese i Linux-[dokumentasjonen][linux] om Console Controls:

-------   -----------------------------------------
ESC (     Start sequence defining G0 character set
          (followed by one of B, 0, U, K, as below)

ESC ( B   Select default (ISO 8859-1 mapping).

ESC ( 0   Select VT100 graphics mapping.
-------   -----------------------------------------

Jeg liker at plattformer har [bakoverkompatibilitet][],
og dette er et heftig eksempel.
Jeg kan her nyte, rett ut av boksen, innhold og kontrollkoder
produsert for 40 år siden, på min moderne Mac eller Linux-maskin.

La oss i denne adventstiden sette pris på all kode og innhold
skapt i 2023 og tidligere, som fortsatt virker som det skal 🌟

Min gave til dere i denne kalenderluken er julehilsen fra fortiden:

```
printf '\033[?25l' # skjul markør
curl -s http://artscene.textfiles.com/vt100/xmas2.vt \
| node -e 'require("fs").readFileSync(0).forEach((b,i)=>
    setTimeout(()=>process.stdout.write(Buffer.from([b]).toString()),i))'
```

—Richard Tingstad


P.S. Bonus: Lag kule Git-meldinger:

```
$ git commit -m "$(printf '\033(0 hello \033(B world')"
[master 8253c70]  ␤␊┌┌⎺  world
```


[arkiv]: http://artscene.textfiles.com/vt100/
[ECMA-35]: https://www.ecma-international.org/wp-content/uploads/ECMA-35_2nd_edition_january_1980.pdf ("ESC 2/8 F [...] designate sets of 94 characters which will be used as the G0 set.", § 5.3.7)
[linux]: https://man7.org/linux/man-pages/man4/console_codes.4.html
[bakoverkompatibilitet]: https://mikrobloggeriet.no/olorm/olorm-39/ (GitHub brakk byggene våre)

