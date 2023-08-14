# Installation
Developing on Mikrobloggeriet requires some dependencies to be installed.

## Brew

### Clojure
Mikrobloggeriet is developed in [Clojure]. 
```shell
brew install clojure/tools/clojure
```
For more information on installing Clojure, read the official [Clojure installation Guide](https://clojure.org/guides/install_clojure).

Clojure runs on the Java Virtual Machine (JVM), meaning a JDK build must be installed as well. We recommend using [Temurin] as it has a permissive license and is activly maintained.

```shell
brew install --cask temurin
```

### Pandoc
Mikrobloggeriet uses [Pandoc] to convert from Markdown-format to HTML when publishing blog posts. 
```shell
brew install pandoc
```

## CLI
To install dependencies related to the CLI, read the [CLI Quickstart].


## Development environment
### VSCode
To connect VSCode to the Clojure REPL, we recommend installing the VSCode extension [Calva].

1. In the Command Palette, search for **Calva: Start a Project REPL and Connect**

2. For *Project Type*, select **deps.edn**
3. For alias, select **:dev**

This will make Calva start a REPL. In the REPL, evaluate the function `(start!)` to start the server for mikrobloggeriet. 

[Temurin]: https://adoptium.net/en-GB/
[CLI quickstart]: cli-quickstart.md
[Calva]: https://calva.io