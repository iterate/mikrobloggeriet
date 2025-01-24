# Linjer i fil A som ikke er i B

Hvis du har to tekstfiler, A og B,
hvordan finner du alle linjene i A som _ikke_ er i B?

![A - B](https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Venn0010.svg/320px-Venn0010.svg.png "https://no.wikipedia.org/wiki/Fil:Venn0010.svg")

(Et eksempel-brukstilfelle er å filtrere ut linjer som allerede er behandlet.)

Mitt goto-verktøy er grep 👇


## grep

```
grep -F -f B -v < A

# search a file for a pattern
# 
#   -F, --fixed-strings
#     Match  using fixed strings. Treat each pattern specified as a string
#     instead of a regular expression.  (-F is specified by POSIX.)
# 
#   -f  pattern_file, --file=pattern_file
#     Read one or more patterns from the file named by the pathname
#     pattern_file. Patterns in pattern_file shall be terminated by a <newline>.
#     (-f is specified by POSIX.)
# 
#   -v, --invert-match
#     Select lines not matching any of the specified patterns.
#     (-v is specified by POSIX.)
```

`grep` er generelt veldig rask, men med stor `pattern_file` så går det sakte.
Hvis filene kun består av ASCII kan vi bruke prefiks `LC_ALL=C grep` for å øke ytelsen noe,
men det hjelper ikke så mye.
Hvilke andre alternativ har vi?


## hash set

Ett nivå lavere enn å kjøre en database er å bare huske linjene i minnet vha. f.eks. Python, Node.js eller _awk_:

```
awk 'BEGIN{
  while(getline < "B") seen[$0]++
  close("B")
}
!seen[$0]' < A
```

Awk består av ett eller flere `pattern{ action }`
der `BEGIN` er et spesielt pattern som kjører før input (A) behandles,
og `!seen[$0]` får default action `{ print $0 }` siden ingen er angitt.
(`$0` er ei tekstlinje.)

Hvis du ikke vil holde hele B i minnet eller
bruke et ekstra programmeringsspråk
finnes det noen flere enkle kommandoer. 👇


## join

Hvis `A` og `B` har én identifiserende kolonne (eller ett ord),
kan vi bruke `sort` og `join`:

```
sort -oA A
sort -oB B

join -v 1 A B

# join - relational database operator
#
#  The join utility performs an “equality join” on the specified files and
#  writes the result to the standard output.  The “join field” is the field in
#  each file by which the files are compared.  The first field in each line is
#  used by default.  There is one line in the output for each pair of lines in
#  file1 and file2 which have identical join fields.  Each output line consists
#  of the join field, the remaining fields from file1 and then the remaining
#  fields from file2.
#
#    -v  file_number
#      Instead of the default output, produce a line only for each unpairable
#      line in file_number, where file_number is 1 or 2.
```

Hvis du ikke vil tenke på kolonner, kun hele linjer,
er siste kommando perfekt. 👇


## comm

```
comm -2 -3 A B

# compare two sorted files line by line
#
#  three text columns as output: lines only in file1, lines only in file2, and
#  lines in both files.
#
#    -1      Suppress the output column of lines unique to file1.
#    -2      Suppress the output column of lines unique to file2.
#    -3      Suppress the output column of lines duplicated in file1 and file2.
```

Denne visste jeg ikke om før nylig, men den er like grunnleggende som `grep` — fra 1973!
(De nyere join og awk er "bare" fra 1979.)

Jeg synes disse gamle verktøyene er kjempefine og nyttige den dag i dag.

Har du andre måter å filtrere ut eksisterende linjer? Jeg vil gjerne høre.

— Richard Tingstad

