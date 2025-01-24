# Go interface nil values

Vi brente oss på en Go-finurlighet igjen.

Vi returnerte interfacet `io.Reader` fra noen funksjoner,
og vi hadde en sjekk `if reader == nil`.

Dette virker noen ganger fint, og andre ganger ikke.
Se følgende kode:

```
func main() {

    var reader io.Reader = nil
    fmt.Println(reader == nil) // true

    var b *bytes.Reader = nil
    fmt.Println(b == nil)      // true

    reader = b

    fmt.Println(reader == nil) // false 🤯
}
```

`bytes.Reader` implementerer `io.Reader`.
Derfor kopilerer dette uten problemer, og kjører også helt perfekt med ulike verdier, bortsett fra akkurat den vist over.

Det viser seg at implementasjoner av interface er `nil` kun hvis verdien _og_ typen er `nil`.

[Go FAQ](https://go.dev/doc/faq#nil_error) har et innslag om dette og anbefaler bruk av interfaces i returtyper, selv om:

> This situation can be confusing

De har ellers ingen gode løsninger utover:

> Just keep in mind that if any concrete value has been stored in the interface, the interface will not be `nil`.

Jeg synes dette var veldig overraskende og føler at Liskov Substitution Principle (LSP) blir brutt,
men får bare prøve å ha dette i mente.


Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

