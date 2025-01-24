# openr

Jeg har laget et lite skript som jeg bruker ofte.

## Hva?

Det er et skript som åpner en tilfeldig fil i en teksteditor.

Du kan eksplisitt velge teksteditor med `--editor`/`-e`, ellers velger skriptet hva enn du har i `$EDITOR`.

Om du bruker `vi`, `vim`, `nvim`, `lvim` får du også åpnet fila på en tilfeldig linje.

Om du bruker `hx`, `code`, `codium` får du også åpnet fila på en tilfeldig kolonne på en tilfeldig linje.

## Hvordan?

```fish
function openr
  argparse e/editor= -- $argv
  or return

  if set -ql _flag_editor
    set editor $_flag_editor
  else
    set editor $EDITOR
  end

  set file       (git ls-files | shuf -n1)
  set line_text  (cat -n $file | shuf -n1)
  set line       (string sub -l6 $line_text | string trim)
  set position   (shuf -n1 -i1-(string sub -s6 $line_text | gwc -c))

  switch $editor
    case hx
      $editor $file:$line:$position
    case vi vim nvim lvim
      $editor $file +$line
    case code codium
      $editor --goto $file:$line:$position
    case '*'
      $editor $file
  end
end
```

### Usage

Man kjører programmet slik:

```sh
$ openr
```

eventuelt feks slik:

```sh
$ openr -e emacs
```

eller

```sh
$ openr --editor /Applications/Xcode.app/Contents/MacOS/Xcode
```

### `fish`

`fish`, som dette er skrevet i, er en modernere `sh`/`bash`/`zsh`. Det er mye bedre å skrive i, og er ikke POSIX-kompatibelt. Det vil si at du ikke kan klistre inn vilkårlige shellscripts fra internett og forvente at `fish` kan kjøre det. Men det er uansett ikke så lurt.

### `argparse`

```fish
  argparse e/editor= -- $argv
  or return
```

Her bruker vi `fish'` finfine [`argparse`](https://fishshell.com/docs/current/cmds/argparse.html)-kommando til å definere og _parse_ kommandoargumenter. Vi sier at vi har étt argument, `editor` (kortform: `e`) som har et parameter (`=`), og vi skal lese dette inn fra `$argv` (som er en liste over alle ordene man skriver etter `openr`).

Denne `editor`-verdien blir så stappet i de implisitte variablene `$_flag_editor` og `$_flag_e`.

```fish
  if set -ql _flag_editor
    set editor $_flag_editor
  else
    set editor $EDITOR
  end
```

Her sjekker vi om `$_flag_editor` er oppgitt, og setter isåfall `$editor` til den verdien.

Om `$_flag_editor` _ikke_ er satt setter vi `$editor` til å være hva enn `$EDITOR` er.

### Finne frem filer og posisjoner

```fish
set file       (git ls-files | shuf -n1)
```

Her lister vi ut alle filene git vet om i mappen (og undermapper) man står i med `git ls-files`. Så pipes dette inn i `shuf -n1` som gir oss én tilfeldig linje av det som blir pipet inn, altså et tilfeldig filnavn. Vi setter dette i variablen `$file`.

```fish
set line_text  (cat -n $file | shuf -n1)
```

Her lister vi ut innholdet i fila vi fant i forrige steg. Med `-n` får vi også med linjenumre (som vi bruker i et senere steg.) Så pipes dette inn i en `shuf -n1` som over. Så vi står igjen med `$line_text` som inneholder en tilfeldig linje fra fila i `$fila`, med linjenummer foran.

```fish
set line       (string sub -l6 $line_text | string trim)
```

Her skraper vi ut de seks første tegnene i `$line_text` som vi definerte over, og luker vekk _whitespace_ med `string trim`. Vi sitter igjen med linjenummeret fra den tilfeldige linja i variablen `$line`.

```
set position   (shuf -n1 -i1-(string sub -s6 $line_text | gwc -c))
```

Denne bråkete linja fjerner linjenummeret fra `$line_text` og teller antall tegn med `gwc -c`.
Vi bruker `gwc` over `wc` fordi `wc` på MacOS putter inn _whitespace_ før tallet vårt.
Så tar vi og generer en liste av tall fra `1` til det antallet tegn vi nettopp fant.
Så velger vi oss ut et tilfeldig av disse tallene. Og setter det i `$position`.

Som du kanskje ser er narrativet i koden og prosaen nokså forskjellig her. Beklager om det er vanskelig å følge, men vi sitter i hvert fall igjen med et tilfeldig kolonnenummer.

### `switch`

```fish
  switch $editor
    case hx
      $editor $file:$line:$position
    case vi vim nvim lvim
      $editor $file +$line
    case code codium
      $editor --goto $file:$line:$position
    case '*'
      $editor $file
  end
```

Her velger vi hvordan vi vil starte editoren basert på innholdet i `$editor`.

- Er editoren vår `hx` starter vi `$editor` (`hx`) med en syntaks som spesfiserer linje og kolonne
- Er editoren vår en `vi`-variant, får vi bare linje
- Er editoren vår en `VSCode` får vi både linje og kolonne
- Er editoren ukjent får vi bare fila

## Hvorfor?

Jeg synes det er nyttig å av og til åpne en tilfeldig fil i et prosjekt og lese den og reflektere litt over hva den gjør/er og hvorfor den eksisterer. Er det en gammel bit med kode? Hvordan passer den inn med den nye koden vi skriver? Ser jeg noen åpenbare forbedringer jeg kan gjøre her og nå? Kanskje denne koden kan hjelpe meg å se resten av koden på en litt annen måte?
