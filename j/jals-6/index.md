# JALS-6 - Lage en editor i browseren

Jeg jobber med å lage en editor Unicad for ren tekst (uten formatering). Men vi ønsker å lagre informasjon
om hva brukeren gjør, ikke kun enderesultatet.

Dvs, istedet for å lagre teksten `hei`, vil vi lagre det brukeren gjorde for å komme dit. Om brukeren skriver `hi`, ser
brukeren har gjort en feil, visker vekk `i` og skriver `ei` så det blir `hei`, vil vi lagre:

1. Skrev `h` på posisjon 0
2. Skrev `i` på posisjon 1
3. Visket ut posisjon 1
4. Skrev `e` på posisjon 1
5. Skrev `i` på posisjon 2

Dette for å få til collaborative editing, og for effektivt kontinuerlig kunne sende meldinger frem og tilbake
til serveren.

Det finnes en løsning for dette, og det er et event som heter `beforeInput`. Det er er en event som skjer rett etter brukeren
har gjort en handling, men før handlingen "får effekt" i DOM-en. Her kan vi både se hva brukeren gjør, og også
stoppe handlingen. Dette er det vi bruker for rik-tekst i Unicad.

Denne eventen støttes av `<textarea>`. Så alt ser bra ut. Trodde jeg. Helt til jeg fant ut at når man kobler
dette på en `<textarea>` så fungerer ikke `getTargetRanges()`, som er en funksjon som returnerer _hvor_ endringen skjedde.
Det hjelper ikke om vi vet at brukeren skrev `i` hvis vi ikke vet hvor `i`-en skal være.

Vi kan trikse oss rundt det ved å bruke `<textarea>` sin `selectionStart` og `selectionEnd`. Men disse returnerer hvor selection
er _nå_ (dvs før hendelsen skjedde, siden DOM-en ikke er oppdatert enda), ikke hvor endringen kommer til å skje. 

Om brukeren trykker Ctrl+Backspace vil man i de fleste
browsere viske ut et helt ord. Men for å støtte denne med `beforeInput` i et `textarea` må vi faktisk selv finne ut
hvilket ord brukeren har visket ut, vi får kun vite at brukeren har gjort input action `deleteWordBackward`, og hvor cursoren
stod når brukeren gjorde det.

Det er også flere ting som gjorde koden vanskelig å ha med å gjøre når jeg brukte `textarea` og `beforeInput`. Resultatet var
at jeg måtte endre til en `<div>` med `content-editable=true` som har sine egne quirks, blant annet dårligere støtte for universell
utforming.

Så konklusjonen er: Editering i browseren er broken (men ikke så broken som før vi fikk `beforeInput`, som var så sent som 2021).
Skal du lage en editor i browseren, bruk et tredjepartsbibliotek som CodeMirror (for ren tekst) eller ProseMirror (for rik tekst).
Det er en grunn til hvorfor vi velger å ikke gjøre det i Unicad, selv om vi på ingen måte er sikre på at vi tok riktig valg (men
fremdeles heller mot at vi gjorde det riktige valget). Hvorfor vi ikke bruker CodeMirror og ProseMirror i Unicad får være en annen jals.

Har du spørsmål eller kommentarer, ta kontakt med meg på slack (for Iterate-ansatte) eller på [mail](sindre@iterate.no) (for andre).

-- Sindre

<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
