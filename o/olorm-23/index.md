# Generell find & replace

Search & replace av tekst i filer er kjekt med `sed -i` (in-place):

```
find . -name \*.txt -exec sed -i.bak 's/foo/bar/g' {} \;
```

Men det er ofte kommandoer ikke har et _in-place_-flagg. Da kan man kjøre (eksemplifisert med `jq .`):

```
find . -name \*.json -exec sh -c 'jq . {} >tmp && mv tmp {}' \;
```

Send gjerne spørsmål eller kommentarer til Richard Tingstad :)

