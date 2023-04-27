# Go slice size bug og søk

Jeg innførte nylig en bug i prod:

```
parentIDs := make([]string, len(parents.els))
for _, el := range parents.els {
    parentIDs = append(parentIDs, el.Id)
}
```

Den første linja skulle hatt `make([]string, 0, len(parents.els))`,
slik at _kapasiteten_ blir satt til det siste argumentet, men _lengden_ blir satt til 0.
Dette fordi `append` legger til elementer _på slutten_ av slice-en.

Jeg gjorde så et søk i kodebasen etter flere tilfeller:

```
find . -name \*.go -exec \
    awk '/= make\(\[\][^,]*,[^,]*$/ {    # linjer med "= make([]" og ett komma
        if ($1 == "var")                 # hvis 1. ord er "var":
            for (i=1;i<NF;i++) $i=$(i+1) # dropp første ord (som `shift`)
        if ($4 != "0)")                  # hvis siste arg != 0:
            v = $1                       # sett 'v' til variabelnavnet
    } v && $0 ~ (v " = append\\(" v) {   # hvis vi finner "v = append(v":
        print FILENAME, NR, $0           # skriv ut filnavn, linjenr og linje
    }' {} \;
```

Det er ikke perfekt, men det er ganske bra.

