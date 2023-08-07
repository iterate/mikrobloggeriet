# Installation
**Work in progress.**

Developing on Mikrobloggeriet.no requires some setup.

## Brew
Mikrobloggeriet is developed in [Clojure]. 
```shell
brew install clojure/tools/clojure
```
For more information on installing Clojure, read the official [Clojure installation Guide](https://clojure.org/guides/install_clojure).


Clojure is primarily hosted on the Java Virtual Machine (JVM), meaning that a JVM is required to compile and execute Clojure code.
We recommend using [Temurin] as it is free and open source, however other JDK's, such as Oracle Java or OpenJDK will most likely also work.

```shell
brew install --cask temurin
```

Mikrobloggeriet uses [Pandoc] to convert from Markdown-format to HTML when publishing blog posts. 
```shell
brew install pandoc
```

To install dependencies related to the CLI, read the [CLI Quickstart](https://github.com/iterate/mikrobloggeriet/blob/master/cli-quickstart.md).

TO-DO: Write VSCode Clojure set-up.