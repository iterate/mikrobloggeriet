# Unminifying av kode med LLM
Akkurat nå jobber jeg med å skrive om noe funksjonalitet i en gammel app. Spesifikt skal jeg bytte ut kartleverandør. I bunn er det leaflet som brukes til å vise kartene, men leaflet lå bak en wrapper fra den forrige kartleverandøren. Derfor var det ikke bare å erstatte det gamle kart-endepunktet med det nye. Jeg var derimot avhengig av å vite hva som skjedde bak kulissene, men wrapper-koden var minified. Da er den ikke særlig lesbar.

For en stund tilbake kom jeg over [denne artikkelen](https://glama.ai/blog/2024-08-29-reverse-engineering-minified-code-using-openai) på HN, som i bunn og grunn er en bedre versjon av dette blogginnlegget jeg nå sitter å skriver.
Poenget er at LLMer er overraskende gode på å unminifye kode, og det kan man bruke dersom man plutselig har behov for å forstå noe minified kode.

Jeg limte  koden min inn i _Claude_, og ut fikk jo en penere formatert kode, litt mer deskriptive variablenavn, og noen kommentarer. Kommentarene var ikke alltid riktig, men nå manglet jo modellen litt kontekst også.
Uansett fikk jeg lesbar kode ut, som jeg kunne bruke til å forstå hva jeg trengte å ta med videre når jeg skulle skrive meg ut av denne wrapperen.
