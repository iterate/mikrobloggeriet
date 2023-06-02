# OLORM-25: Markdown-lenker på tre forskjellige måter

Markdown støtter å lage lenker.
Men du kan lage lenker med forskjellig type notasjon!

Her er tre alternativer.

Metode 1: *Inline-lenker*:

Markdown:

```markdown
[teodor](https://teod.eu)
```

HTML:

> [teodor](https://teod.eu)

Metode 2: *Referanse-lenker med valgt navn*:

```markdown
[teodor][1]

[1]: https://teod.eu
```

HTML:

> [teodor][1]
>
> [1]: https://teod.eu

Metode 3: *Referanse-lenker med implisitt navn*.

Markdown:

```markdown
[teodor]

[teodor]: https://teod.eu
```

HTML:

> [teodor]
>
> [teodor]: https://teod.eu

Av de tre, foretrekker jeg referanse-lenker med implisitt navn.

1. Det er minst kode
2. Det innfører _færrest antall navn_ i koden.
   Med andre ord, det minimerer mengden abstraksjon.
3. Det utfordrer meg til å gi lenken min et godt navn.
4. Det blir minimalt med visuell støy rundt lenken.
   Gigantiske lenker midt inni et avsnitt, som `[til en fil](https://github.com/iterate/olorm/tree/65be6088ae2b54b4b7e9413acaed8327d220ec84/serve/src/olorm/devui.clj)` gjør at avsnittet blir vanskelig å lese når man leser plaintext.

Såvidt jeg vet støttes alle tre i [Github Flavored Markdown] og i [CommonMark].
Jeg foretrekker å bruke [Pandoc] til å jobbe med Markdown.
Her i [Mikrobloggeriet] bruker vi Pandoc til å konvertere Markdown til HTML.

—Teodor

[Github Flavored Markdown]: https://github.github.com/gfm/ 
[CommonMark]: https://commonmark.org/

[Pandoc]: https://pandoc.org/
[Mikrobloggeriet]: [https://mikrobloggeriet.no/]
