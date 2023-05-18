# CLI for publishing

What if contributing to a technical blog as as simple as running a command, writing some text, saving and quitting?

That's our goal.

    olorm create
    # edit in your #EDITOR
    # :wq - or save and quit in some other way
    # That's it!

## prerequisites

Please install [babashka] and [bbin].
If you are using macOS, you may use Homebrew:

    brew install borkdude/brew/babashka
    brew install babashka/brew/bbin

[babashka]: https://babashka.org/
[bbin]: https://github.com/babashka/bbin

## install the OLORM CLI

From this directory, please run:

```shell
bbin install . --as olorm --main-opts '["-m" "mikrobloggeriet.olorm-cli"]'
```

## install the JALS CLI

From this directory, please run:

```shell
bbin install . --as jals --main-opts '["-m" "mikrobloggeriet.jals-cli"]'
```
