# Javascript og React 101

## Bakgrunn
Jeg har overhørt flere (spesielt designere) si at de gjerne skulle lært seg litt Javascript og React. Det kan være for å bedre forstå mulighetsrommet, bedre kommunisere med utviklere, lage prototyper, endre enkle ting i koden selv, eller bare av nysgjerrighet. 

Uavhengig av hva motivasjonen din er, så kan det virke litt overveldene å finne et sted å starte. Og hvor man skal starte vil jo avhenge helt av hvordan man liger å angripe ting, og hva man kan fra før. De fire viktigste tingene å ha en forståelse for i mine øyne når man lager interaktive nettsider er HTML, CSS, Javascripg og eventuelle rammeverk (f.eks. React). 

Personlig mener jeg at rekkefølgen man burde introduseres til de ulike delene er

1. HTML (HyperText Markup Language) 
2. CSS (Cascading Style Sheets)
3. JS (JavaScript)
4. Rammeverk

Denne artikkelen fokuserer kun på grunnleggende ting i punkt 3 og 4. Å kunne HTML og CSS er ikke et krav for å forstå denne artikkelen, men min oppfordring er likevel å lære seg det grunnleggende der før en begynner på JS og rammeverk. 

Målet med denne artikkelen er ikke å være en komplett guide, men mer en kjapp gjennomgang av konsepter jeg mener er viktig å forstå, slik at en har noen knagger å henge ting på og eventuelt noen nye spørsmål en kan stille en utvikler eller Google.

## Nyttige resurser

### MDN
[MDN (Mozilla Developer Network)](https://developer.mozilla.org/en-US/docs/Web) har veldig god dokumentasjon av HTML, CSS og Javascript med eksempler.


## Kommentarer
```javascript
// Dette er en kommentar. Alt etter // på en linje blir en kommentar og ikke en del av koden. 

/*
 Dette er en annen måte å skrive kommentarer på. 
 Den egner seg godt hvis du vil ha mange linjer med kommentar siden den har en start og en slutt.
 Brukes ofte hvis man har mye kode man vil midlertidig ta bort eller ignorere. 
 Eventuelt til å dokumentere koden hvis man har mye å si
 */
```

## Variabler og konstanter
```javascript
/**
 * var er kort for variabel og er verier som man kan endre på. 
 * "var" sier at nå lager du en ny variabel. 
 * myVar er navnet på variabelen. 
 * Og 1 er verdien den får
 */
 var myVar = 1; 

 /**
  * Etter variabelen er laget trenger du ikke skrive "var" lengre når du skal gjøre ting med den
  */
myVar = 2; // Etter du har laget


// const, eventuelt konstant på norsk, er en verdi som ikke skal endre seg. Så nå vil alltid myConst være tallet 2.
const myConst = 2; 

myConst = 3; // <- Dette er ikke lov og vil krasje, siden det er en konstant og ikke en variabel. 

```

## Datatyper
```javascript
 /**
  * !! For enkelhetsskyld blir det her en sannhet med modifikasjoner. Om en ønsker en mer teknisk riktig forklaring 
  * av datatypene til Javascript kan de leses her: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Data_structures !!
  * 
  * De vanligste datatypene i Javascript er Number, String, Boolean, Array og Object
  */

const myNumber = 12; 
// myNumber er av typen Number

const myString = "Hei"; 
// String (tekst) markeres start og slutt med enten ", ` eller '. Dvs de fleste fnutter kan brukes. Forskjellen på dem er tema for et annet kurs

const myBoolean = true; 
// Bolske verdier kan enten være true eller false. 

const myArray = [12, "Hei", true, myBoolean]; 
// arrays er enkelt og greit lister. De kan inneholde alle de andre datatypene. Firkantklammer markerer start og slutt, og komma sperarerer elementene i lista

const myObject = { navn: "Iterate", alder: 12, ansatte: ["Meg", "Deg", "Hen"] }; 
// Objekter kan også inneholde de andre datatypene, men i motsettning til en liste så er alle verdiene i objektet navngitte.

const myName = myObject.name; 
// Måten man henter ut en verdi fra et objekt er med å skrive navnet på objektet, punktum, og så navnet på feltet man vil ha. Så her blir myName satt til "Iterate"

/**
  * (Warning, litt teknisk info)
  * Javascript er "løst typet". Det vil si at variablene og konstantene har strengt tatt en type, 
  * men man trenger ikke definere den når man lager den, og for variabler så kan den endre seg. 
  * Dette skiller seg fra feks Java og TypeScript hvor man må si hva slags type en variabel er når man lager den, 
  * og den kan aldri være noe annet.
  */

 var year = 2020; // Her er year et tall
 year = "tjuetjue"; // Mens nå ble det endret til å være en tekst (string)
 // Detter er fult lov i Javascript, men ikke lov å gjøre i Java, Typescript etc. Å endre på typen til noe på denne måten kan ofte resultere i morsomme bugs, men gir også en frihet. 
```

## Funksjoner
```javascript
/**
 * Funksjoner defineres som vist under
 * function <- sier at dette er en funksjon
 * myFunction <- navnet på funksjonen
 * () <- det som er inni parantesene definerer hva funksjonen tar i mot når den kalles (vi kommer tilbake til dette)
 * {  } <- det som er inni krøllparantesene er selve funksjonen
 */
function myFunction() {

}

myFunction(); // etter funksjonen er laget kan den kalles med å skrive navnet etterfulgt av paranteser.

/**
 * Men myFunction gjør jo ikke noe som helst. Så la oss se på hvordan man kan lage en funksjon som legger sammen to tall. 
 */

function sum(price, tips) { // Denne funksjonen tar inn to verdier når den kjøres. price og tips
    return price + tips; // så returnerer den tilbake summen av dem
}

const total = sum(1000, 100); // total blir her da 1100. Merk at siden sum forventer to verdier må vi ha to verdier mellom parantesene når sum kalles

/**
 * Funksjoner kan også defineres som en konstant i steden for å bruke function sytaxen. 
 * Det gjøres med en såkalt "arrow function" (fordi den har en liten pil i syntaxen).
 * 
 * Dette er en veldig vanlig måte å lage funksjoner på i React-verden
 */

const subtract = (price, deduction) => { 
    return price - deduction;
}

const reducedTotal = subtract(1000, 100); // Her blir da reduced total 900
```

## Conditions
```javascript
 // ----------------------------------------------------------------
 // Conditions


 /** 
  * Hvis man vil at noe kun skal skje i noen tilfeller kan man bruke if-statements
  * Merk at Javascript er litt rar på sammenliginger. Så når man vil sammenligne to ting vil man ca alltid bruke === og ikke ==
  * Spør en voksen eller google om du vil vite mer. 
  */
 if (a === b) {
    // do something
 } else if (b === c) {
    // do something
 } else {
    // do something else
 }

 /**
  * Ofte i React hvis man skal gjøre enten a eller b (og aldri ha c, d, e,...) så bruker man en litt kortere og mindre lesbar måte å skrive if-else
  */

 const y = a === b ? "a er lik b" : "a er ikke lik b"; 
 /**
  * Det som skjer over er at man sier at man skal ha en const y, 
  * og dersom a er det samme som b, så skal y settes til "a er lik b" 
  * og hvis ikke skal den settes til "a er ikke lik b". 
  * 
  * Dette brukes mye i react. For eksempel hvis en venter på data kan man ha noe i duren:
  * isDataDoneLoading ? showUI : showSpinner
  * For å kun vise UIet hvis man er ferdig å laste
  */

```

##  🎉 Tada 🎉
Nå kan du Javascript!
Eller... i hvert fall nok til å lese en hel del av det!

## Imports
For å gjøre kode fra andre filer eller rammeverk tilgjengelig i fila må de importeres. 

```javascript
 /**
  * Stort sett alle filer med React kode starter med Import React from "react". 
  * Her sier man essensielt at man skal gjøre React biblioteket tilgjengelig i fila. 
  * 
  * Når det bare står import x from "y"; så importerer man ALT som er i "y".
  * En annen måte er å skrive import {a, c} from "z";, da henter man bare a og c, men ikke b fra z
  */

import React from "react"; 
// Her skjønner Javascript at dette er et rammeverk som skal importeres og vet hvor React ligger

import { sum } from "./helpers"; 
// Her henter vi ut funksjonen sum som vi lagde i stad fra en fil som ligger i samme mappe som denne fila og heter "helpers". "./" betyr "i denne mappa". En annen vanlig syntax å se er "../helpers" som betyr "helpers i mappa ett nivå opp fra denne"
```

## React
React er litt forenklet et rammeverk som oversetter JSX til HTML. JSX ligner veldig på HTML, men man har muligheten til å gjøre veldig mye mer dynamiske ting med det. 
```javascript
/**
 * Her definerer vi opp en React komponent. En "React komponent" er stort sett bare en funksjon som returnerer JSX. 
 * JSX er syntaxen for å skrive (nesten)-HTML i Javascript. 
 */


const MyReactComponent = () => {
    const myName = "Test Testson"
    const age = 25;

    return (
        <div>
            <h1>Hello {myName}</h1>
            {age > 20 ? <p>Velkommen!</p> : <p>!!! Ingen under 20 slipper inn</p>}
        </div>
    );
}

/**
 * I JSX kan man bruke krøllparanteser til å skrive javascript inni layouten din. Dette er ikke mulig på samme måte i vanlig HTML. 
 * I eksempelet over bruker vi en if-else for å sjekke om variabelen age er større enn 20, og i så fall vise en velkomstmelling, hvis ikke viser vi en sint melding
 * 
 * Vi putter også "Test Testson" inn i tittelen på sida gjennom en konst. Dette er typisk slik man viser navnet på den innloggede brukeren dynamisk.
 */
```