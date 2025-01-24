# En hyllest av JSON

XML og YAML har noen nyttige funksjoner som JSON ikke har, men har også mye mer kompleksitet.

JSON er så lite komplisert at [spesifikasjonen](https://www.json.org/json-en.html) kun består av fem enkle diagrammer.

Dette gjør at jeg nettopp kunne skrive et enkelt testscript helt uten noen JSON parser:

```
curl -s 'http://address' | tee tmp/data.json
if tr -d ' \t\n\r' < tmp/data.json |
    grep '"type":"redirect"' |
    grep '"action":301' |
    grep '"redirectUri":"https://address"'
then
    echo 'Test OK'
fi
```

Kjernen av koden er kommandoen `tr -d` som fjerner alle lovlige whitespace-tegn fra JSON-dataen og slik normaliserer den. (Merk at whitespace _inni_ tekststrenger også fjernes, som kan være problematisk i andre tilfeller.)

Denne koden (med unntak av `curl`) er en gyldig [POSIX](https://pubs.opengroup.org/onlinepubs/9699919799/) shell-kommando og vil derfor virke på alle CI-systemer etc. i all overskuelig fremtid.

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

