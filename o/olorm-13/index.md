# AWK detaljert detalj

Det er overraskende vanskelig å finne nedlasting-størrelsen av et Docker-image,
men noen skrev at man kan bruke `manifest inspect`:

```
docker manifest inspect -v $image | awk '/"size":/{s+=$2}END{print s/1024/1024 " MiB"}'
```

Her avhenger jeg av at `"size": 123` er på hver sin linje, noe som ikke er urimelig.

Men jeg ble også nysgjerrig på: her summeres `123,` uten problemer. Hvor trygt er det?
Til og med dette virker:

```
echo "1432kroner" | awk '{ print int($1/100) " hundrelapper" }'
14 hundrelapper
```

[Spesifikasjonen](https://pubs.opengroup.org/onlinepubs/9699919799/utilities/awk.html#tag_20_06_13_02) har noen kompliserte regler for om feltverdien tolkes som tekst, tall eller _numeric string_.
Det viser seg at det ikke er så viktig, fordi `+`-operatoren alltid er Numeric, og:

> the value of an expression shall be implicitly converted to the type needed for the context in which it is used. A string value shall be converted to a numeric value either by the equivalent of the following calls to functions defined by the ISO C standard:
>
> setlocale(LC_NUMERIC, "");
> numeric_value = atof(string_value);

Funksjonen [atof](https://pubs.opengroup.org/onlinepubs/9699919799/functions/atof.html) er spesifisert som:

> The call atof(str) shall be equivalent to:
> strtod(str,(char **)NULL),

og [strod](https://pubs.opengroup.org/onlinepubs/9699919799/functions/strtod.html):

> decompose the input string into three parts:
>
> 1. An initial, possibly empty, sequence of white-space characters (as specified by isspace())
>
> 2. A subject sequence interpreted as a floating-point constant or representing infinity or NaN
>
> 3. A final string of one or more unrecognized characters, including the terminating NUL character of the input string
>
> Then they shall attempt to convert the subject sequence to a floating-point number, and return the result.

Å lese spec'en er tidvis tungt siden det er så tett knyttet til C-koden.

Men vi kan konkludere: Det er trygt å anta at alle ukjente tegn etter tallet blir ignorert :-)


P.S. Du vil kanskje filtrere `docker manifest` på `platform.architecture`.

