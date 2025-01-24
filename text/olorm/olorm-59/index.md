# Unngå forgrening!

Jeg har blitt mye mer var på når jeg innfører forgrening i kode enn før.
Men hva i all verden skal det egentlig bety?

Å "innføre en gren" betyr noe mer enn å kjøre `git switch --create`, ikke sant?

## De første grenene springer ut fra trestammen

Ordet "branch" på norsk betyr "gren".
Trær har grener.

![_Vachellia erioloba_ er et tre, bilde fra Wikimedia (CC-BY-SA 4.0), https://en.wikipedia.org/wiki/Branch#/media/File:Kameldornbaum_Sossusvlei.jpg](/images/Kameldornbaum_Sossusvlei.jpg)

Et tre har én trestamme.
Når trestammen først _forgrener_ seg, får vi grener ut fra trestammen.

Trestammen _forgrener_ seg og blir til flere grener.
Grener kan også forgrene seg i flere grener.

## Hver `if` lager en ny gren i kodebasen

Før hadde denne koden én logisk flyt.
Nå har den to!

Hver gang koden skal feilsøkes, må vi skjønne begge grenene i koden.
Kunne vi latt være å forgrene?
Når vi forgrener fra nye grener gjør vi det enda vanskeligere for oss selv.

Jeg liker å forgrene _i starten_, og deretter la koden flyte så rett som mulig.

## Hver `git switch --create` lager en ny gren i hodet til utviklerne

Vi stopper ikke alltid ved skillet mellom lokal kode og produksjon.
Git kan holde styr på historikken til koden.
Git kan også la oss lage så mange grener vi ønsker.
Det er ingen grenser!
Jo flere grener vi lager, jo flere grener må vi kjenne forskjellene mellom.
Og når vi skal slå sammen to grener (anti-forgrene?), får vi alle forskjellene tilbake i fleisen.

Hvis vi lar være å forgrene, slipper vi å holde styr på alle grenene.

## Unngå forgrening ved å unngå uferdig arbeid

"Reduser uferdig arbeid" (reduce work in progres) sies ofte.
Men hvorfor?
Folk har mange grunner.
Her er en kandidat til den egentlige grunnen:

> Uferdig arbeid forgrener.
> Forgrening er dumt.

Jo mer uferdig arbeid vi har, jo mer forgrening er det på alle plan.
Hvis vi i stedet gjør færre ting samtidig, gjør de tingene helt ferdig, før vi flytter fokus, kan vi fokusere.
Da er det lettere for oss å få gjort ting!

