# Go Single Method Interface og Adapter

(Tilsvarer Kotlin og Javas Single Abstract Method (SAM) interface / **functional interface**)

Grensesnitt med en enkelt funksjon er veldig praktiske:

```
type Greeter interface {
    Greet(name string) string
}
```

De kan enkelt sendes som parametre eller returverdier til og fra funksjoner (høyere ordens funksjoner),
de kan _komponeres med andre interfaces_, og de er relativt enkle å implementere.

Man kan dessverre ikke ([ennå](https://github.com/golang/go/issues/47487) hvertfall) implementere en SMI (Single Method Interface) direkte fra en funksjon, men man kan lage et Adapter. Vi lager først en funksjonstype med samme signatur som interface-funksjonen:

```
type GreetFunc func(string) string
```

og deretter implementere interfacet på denne:

```
func (f GreetFunc) Greet(name string) string {
    return f(name)
}
```

Nå kan vi hvor som helst enkelt implementere `Greeter` fra en funksjon:

```
func main() {
    greeter := GreetFunc(func(s string) string {
        return fmt.Sprintf("Hello, %s", s)
    })
    greeting := greeter.Greet("Bob")
    fmt.Println(greeting)
}
```

Du har sannsynligvis brukt denne teknikken allerede vba. [http.HandlerFunc](https://github.com/golang/go/blob/go1.20/src/net/http/server.go#L2114-L2123).

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

