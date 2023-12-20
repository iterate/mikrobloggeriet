# Java 21 pattern matching

Pattern matching i Java har blitt bra!

[JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)

For eksempel, la oss lage en ny `Optional`-type av records og sealed interface:

```
sealed interface Maybe<T> {}
record Some<T>(T val) implements Maybe<T> {}
record None() implements Maybe {}
```

(En `record` er bare en enkel data-klasse
som ikke kan utvides eller muteres.
På grunn av `sealed` vet kompilatoren nå at `Maybe` _kun_
kan bestå av `Some` og `None`.)

Det stilige nå er at vi kan gjøre `switch`
som sikrer håndtering av alle mulige tilfeller:

```
String fun(Maybe<Integer> n) {
    return switch (n) {
        case None ignore -> "nothing";
        case Some(var i) -> String.format("%d.0", i);
    };  // ^begge trengs for å få kompilert!
}
```

Det er mulig å nøste og kombinere,
la oss lage en til type:

```
public sealed interface Contact {
    record Email(String mail) implements Contact{}
    record Phone(int prefix, int number) implements Contact{}
}
```

Og vi kan matche slik:

```
void handle(Maybe<Contact> c) {
    switch (c) {
        case None none -> {}
        case Some(Contact.Email e) -> sendEmail(e.mail);
        case Some(Contact.Phone p) -> phone(p.number);
    }
}
```

Om vi ønsker kan vi matche `Phone(int pre, int num)` i stedet for `Phone p` også :)

(P.S. Hvis de ulike typene ikke ligger samme sted som `sealed`-interface-et/klassen
trengs `permits`: `sealed interface Contact permits Email, Phone {`)

