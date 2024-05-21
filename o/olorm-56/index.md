# OLORM-56: Dit og tilbake igjen—TDD, TCR fra en REPL og tilbake til TDD

Når er det lurt å skrive tester?
Hvordan skriver man tester?
Hvorfor skriver man tester?

Effektiv enhetstesting i praksis er lettest å lære fra noen som har jobbet effektivt med enhetstesting før.
Jeg prøver meg alikevel på en en tekst.
Mest historiefortelling, bittelitt enhetstesting i praksis.
Spenn deg fast!


Som utviklere kan vi oppnå en vanvittig effektivitet ved å kontinuerlig vite om systemet vi jobber på er rødt eller grønt, holde oss på grønn, og bli i flytsonen mens vi skriver kode.
Tre teknikker du kan bruke for å komme nærmere flyt når du koder er test-dreven utvikling (TDD), test && commit || revert (TCR) og REPL-dreven utvikling (RDD).
Hva betyr disse egentlig?

I dag får dere høre om min reise fra TDD til TCR og RDD, og tilbake igjen til TDD.

![Dit og tilbake igjen](https://github.com/iterate/mikrobloggeriet/assets/5285452/147bb752-45d1-4d9a-93e7-d6268795f0b0)

## TDD for dimensjonering av armering i betong

Den første kodebasen jeg jobbet på etter endt utdanning regnet ut nødvendig mengde armering per løpemeter for betongdekker i Python.
Da jeg tok over koden hadde koden null tester.
Jeg ble overrasket over at utvikleren turte å implementere denne logikken uten tester.
Hva om utvikleren regnet feil?
Da kunne jo bygg bli dimensjonert feil?

Det første jeg gjorde i den kodebasen var å innføre tester.

Jeg gikk svært sakte fram, og sjekket hva koden gjorde i dag.
Og jeg snakket med en eldre byggingeniør med cirka 40 års erfaring med dimensjonering av betongkonstruksjoner.
Sammen bygde vi en forståelse for hva koden skulle gjøre.

Etter at vi hadde innført tester i koden, var det tryggere for meg å endre koden.
Testene lot meg sove godt.

Folk legger mange ting i testdreven utvikling, kjent som _TDD_ (fra Test-Driven Development på engelsk).
Én av definisjonene er at når du koder, gjør du følgende:

1. Skriv en ny test som vil bli grønn når du har implementert noe ny kode
2. Skriv kode som gjør at testen blir grønn på enklest mulig vis
3. Observer at testene er grønne, eventuelt gjør at testene blir grønne
4. Rydd i koden så det er tydelig hva koden gjør (kjent som "refactoring" på engelsk).

Man må ikke nødvendigvis skrive test før implementasjon.
Men hvis du har tester på koden din, har du bedre kontroll på hva koden gjør.
Da er det lettere å rydde i koden, og utvide koden til å gjøre nye ting.

## TCR med Elm

Elm er et vakkert, ryddig, lite programmeringsspråk for å lage webapper.
[Elm-guiden] er den beste introduksjonsguiden til et programmeringsspråk som jeg noen sinne har lest.
Jeg synes Elm var så bra at jeg lagde og gjennomførte [et kurs i Elm-programmering for barn], og [snakket om erfaringene på Oslo Elm Day 2019].

Da jeg startet i Iterate fikk jeg jobbe litt med Lars Barlindhaug på Woolit-kodebasen.
Vi skrev Elm sammen, og prøvde TCR.
Det passet bra, fordi Woolit er skrevet i Elm, Elm er godt egnet for TCR, og Lars var med på bootcampen der TCR ble funnet opp.
Lars skriver om sin opplevelse med bootcampen på [How to test && commit || revert].

TCR med Elm var en fryd.
Typesystemet til Elm er svært kraftig, og når man programmerer Elm sånn Elm er ment til å bli programmert, er det tilnæmet umulig å innføre feil i Elm.
En ting som ofte sies om Haskell (et annet programmeringsspråk) er "if it compiles, it runs".
Hvis det kompilerer, funker det.
Min erfaring er at det stort sett stemmer for Haskell, og at det ~alltid stemmer for Elm.
Elm har et mer konsistent typeystem enn Haskell som er lettere å sette seg inn i ved å ha færre features.
Et eksempel er typeklasser, typeklasser er en løsning for polymorfisk dispatch i Haskell.
Les [wikipedia.org/wiki/Expression_problem] for mer info.
Philip Wadler nevnes tidlig i Wikipedia-artikkelen, han er en av personene bak Haskell.
Elm har _ikke_ typeklasser.
Det gjør Elm-kode lettere å lese og lettere å sette seg inn i enn Haskell-kode.

[wikipedia.org/wiki/Expression_problem]: https://en.wikipedia.org/wiki/Expression_problem

Lars og jeg satte opp TCR til å kjøre "test" som typesjekk.
Vi skrev kode, lagret, og gikk kun framover hvis testene var grønne.
Det utfordret meg til å tenke i mindre inkrementer.

Litt senere fikk jeg den samme leksa inn med teskje av å jobbe med Oddmund Strømme.
Jeg hadde for vane å endre all koden, og være på rød lenge.
Det har jeg nå gått tungt bort fra.
Nå foretrekker jeg å holde meg på grønn hele tiden, og gjøre refatoreringer som en strøm av kompatible endringer, før jeg til slutt bytter over på ny implementasjon.

Når man gjør dette på teamnivå, kalles det ofte "trunk-based development".

[How to test && commit || revert]: https://medium.com/@barlindhaug/how-to-test-commit-revert-e850cd6e2520
[snakket om erfaringene på Oslo Elm Day 2019]: https://play.teod.eu/lessons-learned-teaching-elm-to-kids/
[et kurs i Elm-programmering for barn]: https://oppgaver.kidsakoder.no/elm
[Elm-guiden]: https://guide.elm-lang.org/

## Umiddelbar feedback for alle kodebaser med REPL

Jeg foretrekker å bruke programmeringsspråket Clojure når jeg kan velge programmeringsspråk.
Det er fordi Clojure er et godt egnet programmeringsspråk for REPL-Driven Development.
REPL-Driven Development blir også kalt _Interaktiv programmering_.
Hvis du er nysgjerrig på Interaktiv Programmering, er presentasjonen [Stop Writing Dead Programs] av Jack Rusher en underholdene introduksjon.

Jeg sporer meg selv av.
Interaktiv programmering er å programmere fra innsiden av programmet sitt.
I stedet for å endre filer som plukkes opp når man rekompilerer eller restarter i en terminal eller med en file watcher, sitter man med en editor koblet til en REPL, der man kan endre oppførselen til egen kode uten å restarte systemet.

Men!
Interaktiv Programmering krever trening og disiplin for å brukes effektivt.
Du kan lett ende opp i en tilstand der filenes tilstand på disk ikke reflekterer tilstanden til programmet ditt i minne.

Det problemet hadde jeg aldri da jeg skrev Elm med TCR.
Jeg visste alltid med 100 % sikkerhet hver gang jeg lagret at koden min passerte typesjekken.
Tilsvarende kunne jeg hatt enhetstester, men det hadde jeg ikke, og det følte jeg ikke at jeg trengte.
Hvorfor kan jeg ikke få til det samme fra en REPL?

[Stop Writing Dead Programs]: https://play.teod.eu/stop-writing-dead-programs/

## TCR fra inni en REPL

Så, jeg prøvde meg på å løse problemet.
Og jeg fikk til det jeg prøvde!
Github-repoet [teodorlu/clj-tcr] beskriver nå hvordan du kan få til TCR i Clojure.

[teodorlu/clj-tcr]: https://github.com/teodorlu/clj-tcr

Trikset er:

1. Lag en ny TCR-snarvei i editor som du bruker i stedet for "evaluér uttrykk" og "lagre fil"
2. Snarveien gjør følgende:
   - Lagre alle filer
   - Synkroniser tilstand i filer til tilstanden til den kjørende prosessen i minnet
   - Kjør testene
   - Reverter hvis testene feiler, commit ellers.
   - Hvis synkronsiering av tilstand fra filene til den kjørende prosessen feiler, reverterer vi da også.

Her er Clojure-kode som gjør nettopp dette:

```clojure
(ns user
  (:refer-clojure :exclude [test])
  (:require
   babashka.process
   clj-reload.core
   cognitect.test-runner))

(defn reload [] (clj-reload.core/reload))

(defn test []
  (let [{:keys [fail error]} (cognitect.test-runner/test {})]
    (assert (zero? (+ fail error)))))

(defn commit []
  (babashka.process/shell "git add .")
  (babashka.process/shell "git commit -m working"))

(defn revert []
  (babashka.process/shell "git reset --hard HEAD"))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn tcr
  "TCR RELOADED: AN IN-PROCESS INTERACTIVE LOOP"
  []
  (try
    (test)
    (commit)
    (println "success")
    (catch Exception _
      (println "failure")
      (revert)
      (reload) ; In those cases where we revert, we choose to clean up our mess
               ; -- don't leave the user with a REPL out of sync with their files.
      )))
```

Som Oddmund tidligere har sagt om TCR, er dette ikke kode man trenger et bibliotek for å bruke.
Tilpass arbeidsflyten til koden og de som skal jobbe med koden.

Så binder du kode i editoren din til å:

1. Lagre filer
2. Kjøre `user/tcr`.

Sånn kan den funksjonen se ut i Emacs Lisp:

```emacs-lisp
(defun teod-clj-tcr ()
  (interactive)
  (auto-revert-mode 1)
  (projectile-save-project-buffers)
  (cider-interactive-eval "(clj-reload.core/reload)")
  (cider-interactive-eval "(user/tcr)"))
```

Så binder du den til en knapp du velger selv.
For å binde til Option+Enter på en Mac som kjører Doom Emacs, kan du gjøre følgende:

```emacs-lisp
(map! :g "M-RET" #'teod-clj-tcr)
```

## TDD 2: Dommedag

![Terminator 2: Dommedag](https://upload.wikimedia.org/wikipedia/en/8/85/Terminator2poster.jpg)

Hvis du ønsker å bli en bedre utvikler enn du er i dag, bør du jobbe med folk du har noe å lære av.
En av utviklerne jeg mener jeg har noe å lære noe av er [Christian Johansen].
Han er en dyktig programmerer som lager gode biblioteker, og har lang erfaring med test-dreven utvikling og parprogrammering.

På Babaska-meetup i Mai fikk jeg par/mob-programmere med Christian.
Han dyttet oss i retning av TDD.
Jeg i starten instrukser som "skriv en test som sjekker X" og "fiks koden så testen er grønn".

Jeg innså at vi fikk mesteparten av verdien jeg hadde fått fra TCR tidligere med god, gammeldags TDD.
Skriv koden så den kan testes.
Skriv en test som viser oppførselen du ønsker.
Kjør testene kjøre, resultatet bør bli rødt.
Skriv kode.
Kjør testene, resultatet blir helst grønt.
Repeat.

Å jobbe med flinke folk er skummelt.
De kan ting som du ikke kan.
Du føler deg kanskje dømt for at du ikke er flink nok ennå!

Men personene du jobber med er helst ikke en robot sendt tilbake i tid for å ta livet av deg, men heller en trygg utvikler som både ser hva du kan gjøre bedre, og også ønsker å investere i at du kan bli flinkere!
Det er _ikke_ til hjelp at noen sier "du, Teodor, du gjør _alt_ feil, og dette er helt håpløst".
I kontrast er det supernyttig når noen viser hvordan de tenker, og stiller spørsmålstegn til rare ting du gjør som du kanskje ikke trenger å gjøre.

[Christian Johansen]: https://cjohansen.no/

## TDD fra inni en REPL

Programeringen med Christian fikk meg til å tenke.
Jeg ønsker meg følgende:

1. En REPL så jeg kan evaluere uttrykk
2. Enhetstester som dekker det jeg bryr meg om
3. En måte å vite at koden jeg kjører er i synk med koden som kjører på disk
4. En umiddelbar måte å kjøre enhetstestene.

Så jeg skrev meg kode for å gjøre nettopp det i min Emacs:

```emacs-lisp
(defun teod-reload+test ()
  (interactive)
  (projectile-save-project-buffers)
  (cider-interactive-eval "(do (require 'clj-reload.core) (clj-reload.core/reload))")
  (kaocha-runner-run-all-tests))

(map! :g "M-RET" #'teod-reload+test)
```

Du kan gjøre omtrent det samme med Visual Studio Code og Calva også:

```json
    {
        "key": "ctrl+[Semicolon]",
        "command": "runCommands",
        "args": {
            "commands": [
                "workbench.action.files.saveFiles",
                {
                    "command": "calva.runCustomREPLCommand",
                    "args": {
                        "snippet": "(do (require 'clj-reload.core) (clj-reload.core/reload))"
                    }
                },
                {
                    "command": "calva.runCustomREPLCommand",
                    "args": {
                        "snippet": "(flush) #_forces-a-print"
                    }
                },
                "calva.runAllTests"
            ]
        }
    }
```

## Ikke test for å teste, test for å gjøre din egen hverdag bedre.

Jeg skriver ikke tester for testene sin skyld.
Jeg skriver tester for _meg selv_ og for _de andre utviklerne på teamet mitt_.
Jeg vil ha et godt utviklingsmiljø lokalt, så jeg kan fokusere på å kode, ikke å stirre på stack traces.
Og jeg vil ha kontroll på at koden i produksjon gjør det jeg tror den gjør.
Derfor skriver jeg tester.

Når jeg møter en kodebase der README sier hvordan jeg kan kjøre testene, og testene dekker det som er viktig i koden min, blir jeg glad.

## Appendix A: les Tolkien!

[There and Back Again] er også tittelen på en bok som er bedre kjent som _Hobbiten_.
Den liker jeg veldig godt!

[There and Back Again]: https://en.wikipedia.org/wiki/There_and_Back_Again_(disambiguation)
