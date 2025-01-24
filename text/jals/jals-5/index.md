# JALS-5: Mange veier til Rom

Som en programmerer både hater jeg og setter pris på på hvordan det samme resultatet kan oppnås på forskjellige måter. Ta for eksempel følgende react-komponenter. 

```
const PermissionCell = ({
  permission,
}: {
  permission: LibraryPermissionType;
}) => {
  const t = useT();
  return (
    <div>
      {(() => {
        switch (permission.scope) {
          case 'owner':
            return t('Administrator');
          case 'write':
            return t('Can edit content');
          case 'read':
            return t('Can use content');
        }
      })()}
    </div>
  );
};
```

```
export const PermissionCell = ({
  permission,
}: {
  permission: LibraryPermissionType;
}) => {
  const t = useT();
  return (
    <div>
      {permission.scope === 'owner' && t('Administrator')}
      {permission.scope === 'write' && t('Can edit content')}
      {permission.scope === 'read' && t('Can use content')}
    </div>
  );
};
```

De utfører alle samme oppgave, men på forskjellige måter. Dette spekteret av muligheter kan noen ganger gjøre koding utfordrende. Likevel gir det også rom for kreativitet og tilpasning. Og mens noen kanskje vil diskutere hvilken metode som er “best”, synes jeg ikke det spiller noen rolle så lenge koden fungerer som den skal og sjeldnere og sjeldnere gidder jeg å ha en mening i pr-reviews så lenge jeg skjønner hva som skjer. 

Det viktigste for meg er at neste utvikler som leser koden forstår hva som skjer. Og det er denne balansen mellom funksjonalitet og forståelighet som er det sanne målet god kode. Som med så mye annet i livet, er det mange veier til samme mål.

(Skrevet 96% av chat gpt) 
