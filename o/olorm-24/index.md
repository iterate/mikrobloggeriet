#  Refactoring is dead

La oss slutte å snakke om _refactoring_.

Her er en uutfyllende liste over hva man kan mene når man sier _refactoring_:

- > Refactoring is a disciplined technique for restructuring an existing body of code, altering its internal structure without changing its external behavior. [[Refactoring.com](https://www.refactoring.com)]
- Å utføre en av de navngitte _refactoringsene_ i Refactoring-boka [[MartinFowler](https://martinfowler.com/books/refactoring.html)]
- Å gjøre en "trygg" automatisk kodeendring/code action i feks IntelliJ
- Å gjøre en vilkårlig automatisk kodeendring i feks IntelliJ
- Å gjøre en manuell "trygg" kodeendring
- Å endre kode uten å brekke tester
- Å rydde i kode
- Fikse linterfeil
- Skrive flere tester
- Optimalisere kode til å kjøre raskere
- Koding som ikke er dekket av en JIRA task
- Koding (hva som helst)

Så selv om du kanskje vet hva du mener når du sier _refactoring_ er sjansen stor for at den som hører/leser ikke vet hva du mener.
Kanskje de erstatter din oppfattelse med sin egen. Eller kanskje de, kloke av skade, erstatter med sin egen og legger til et buffer av vaghet og godvilje.

Når du sier *refactoring* er det sansynlig at mottaker sitter igjen med *mindre informasjon* enn du mente å gi.

<aside>Som en øvelse kan man prøve å bytte ut ordet _refactor_, _reactoret_, _refactoring_ med _smurfe_, _smurfet_, _smurfing_ og kjenne på om man føler at man mister noe informasjon.</aside>

Det er kjedelig å miste informasjon. Men jeg synes det er et større tap at hver gang du sier _refactoring_ kunne du sagt noe annet som er mer eksplisitt og mer beskrivende.
Hvor interessant er det egentlig om endringen din er en _refactoring_ eller ikke? Kan du ikke heller si **hva** du gjorde?

- Erstattet en klump _string literals_ med en Enum-type
- Dro parse-logikken ut i et eget modul
- Sorterte funksjonene så det er lettere å lese dem fra toppen og ned
- Inverterte noen _nesta_ _ifs_ så vi kan gjøre en _earligere early return_
