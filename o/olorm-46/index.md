# OLORM-46: kortsiktig og langsiktig arbeid med kode

I prosjektet jeg jobber på nå, skal jeg inn, gjøre en jobb, og ut igjen innenfor planlagt tid.
Det er mye kode, og jeg har ikke tid til å lære meg alt.
I tillegg har kunde hatt høye krav til hva som må inn, og begrenset budsjett.

Jeg føler på kroppen at jeg ikke er i stand til å gjøre de beste beslutningene om hvordan en ting bør løses i kodebasen.
Jeg har tid til å sette meg _delvis_ inn i eksisterende arkitektur og hvordan det har blitt jobbet, men for å faktisk komme i mål med det jeg skal gjøre må jeg sette strek.
Det er det pragmatiske valget; kunden har et behov, jeg kommer og gjør en jobb, og skal gjøre den jobben til budsjettert tid.
Kodeteknisk _vet jeg_ at det er ting jeg bør ta tak i som jeg ganske enkelt ikke rekker.

Jeg er overbevist om at kode kan være _objektivt god_.

- Fiks en flaky test? Objektivt god forbedring.
- Sørg for at testene kjører i CI? Objektivt god forbedring.
- Redusér tiden det tar å kjøre testene fra 10 sekunder til 0.2 sekunder? Objektivt god forbedring.

Denne typen forbedringer får jeg sjelden tid hvis jeg er "inn og ut" på et prosjekt.

Å bli en god utvikler krever langsiktighet nok til at man rekker å sette seg inn i ting, bli produktiv til å jobbe med koden, så _oppleve_ effekten av arkitekturen på egen effektivitet, så oppleve effekten av _forbedret_ arkitektur.

Akkurat sånn har jeg lært å programmere.
Jeg har satt opp et prosjekt.
Prosjektet har etter hvert løst problemet det skal løse.
Så har jeg merket at enkelte biter av arkitekturen _lugger_.
Så har jeg _endret_ arkitekturen, og fikset problemet.

Bør man lære seg å jobbe med produkter og lære seg å jobbe med kode samtidig?
Jeg mener nei.
Uten god kode er det null sjanse for at vi klarer å lage gode produkter.
Men _med_ svært solid kompetanse på kode, er det _mye_ lettere å lage gode produkter.
Kan du gjøre en solid forbedring på koden på én dag?
Bra.
Er du ikke der ennå?
Da kan ting forbedres!
Du kan forbedres, koden din kan forbedres.

Og når forbedringer på produktet kan shippes på én dag i stedet for på to uker, kan jeg love deg at jobben med å lage et godt produkt blir lettere.

Kjersti skriver om å avfeie kundens i [LUKE-6 - Kunden tar alltid feil!][luke-6]:

> Kanskje vi ville bli bedre på å ta utgangspunkt i kundens hypoteser dersom vi
> samlet sett vet at vi ikke blir «tatt» på å feile litt lenger ned i løypa (ref
> punkt 1). Her har jeg egentlig et veldig stort spørsmål: hvordan balanserer vi
> vårt eget behov for å forstå med kundens forståelse av markedet vi skal inn i?
> Jeg har en følelse av at vi blander kortene her.

Hvis vi ikke tar kundens hypoteser seriøst, bør ikke kunden leie oss inn.

Grav i _hva hypotesen er_.
Vær ekstremt nøye på de spørsmålene.
Men _ikke påstå at det er feil!_
Ikke før du har prøvd.

Tidpunktet å diskutere hypotesene er etter en hypotesetest, ikke før.
Snakk om hva den første effekten vil være hvis det vi gjør går bra.
_Det_ er hva du vil teste.
Snakk om hvilken tidshorisont kunden ser for seg å få til det på.
Flott, da vet du cirka hvor stor denne klumpen med arbeid er.

Så kan du se nøye om det faktisk fungerte ikke når du har prøvd på ekte.

Er dette mye å forvente?
Ja!
Bli gjerne god på å kode først.
Men ikke avfei det kunden sier før dere har prøvd: følg [Principle of charity] før du setter deg selv i kritikkmodus.
Bonus: når du viser villighet til å _prøve_, begynner kunden å stole på deg.
Når kunden stoler på deg, er det mer sannsynlig at du blir hørt.

[luke-6]: https://mikrobloggeriet.no/luke/luke-6/
[Principle of charity]: https://en.m.wikipedia.org/wiki/Principle_of_charity

—Teodor
