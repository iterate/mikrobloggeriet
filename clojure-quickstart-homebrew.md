# Get started with Clojure on Mac with Homebrew

This guide will take you from a Mac with Homebrew without a Clojure development environment, to a Mac with homebrew _and with_ a Clojure development environment.

## Clojure, a JVM and `rlwrap`

First, install Clojure with Homebrew:

```shell
brew install clojure/tools/clojure
```

This will install two executables on your system: `clj` and `clojure`.
`clj` is for you to run in a terminal, and `clojure` is for other applications.
Actually, you can completely forget that you ever heard there is a `clojure` command, as you might never need it.

If you run `clj` in a terminal now, something will crash.
Just try it if you want to see the error message.

You get an error because two system dependencies are missing, a Java Virtual Machine (JVM) in short, and `rlwrap`.

We need a JVM because Clojure runs on the JVM, and has access to the JVM ecoystem.
You can choose any Java version and distribution you want.
In this guide, we will use the _temurin_ java distribution because it's a good choice, and install the latest Java version because it mostly just works, and then we don't have to care about java.
I (Teodor) have never experienced any breaking change in my code resulting from updating a Java version.

Finally, let's install that "Temurin JVM".

```shell
brew install --cask temurin
```

You will see lots of text, then a _very important message_.
The message tells you that "remember to pick java version"!
This is because Apple made support for choosing which java you want, which is a good thing.
Read this message carefully, and select _temurin_ as your java version of choice.

We will now select _temurin_ as our Java version of choice.

## Clojure is meant to be written _interactively_

We do _not_ start and restart the JVM to run new code.
Instead, we _interactively_ load our code changes, and observe new behavior.

To understand what _interactive programming means_, you may either read Erik Sandewall's 1978 paper [Programming in an Interactive Environment: the "LISP" Experience] and Jack Rusher's 2022 talk [Stop Writing Dead Programs].

[Programming in an Interactive Environment: the "LISP" Experience]: https://www.softwarepreservation.org/projects/interactive_c/bib/Sandewall-1978.pdf
[Stop Writing Dead Programs]: https://www.youtube.com/watch?v=8Ab3ArE8W3s

If you _do not_ intend to write your program in an interactive manner, I recommend staying as far away from Clojure and other Lisp dialects as you can.
There are plenty of good programming languages that expect the user to work in a compile-restart-test-cycle.
Try Python, Java, C, C#, Haskell, Elm, Ocaml, Rust, Go, Javascript or typescript, to name some.

_Phew._

## Optional step: run the database on your computer

If you _do want_ a database for local development, set up Docker Compose and run

```shell
docker compose up
```

, then continue.
Keep the terminal where you are running the database open.

## Install Calva: a tool for interactive Clojure programming

[Calva] is a great tool for Interactive Clojure Programming.
Install it from Visual studio code.

There are other good options, but this guide will _not_ cover those options.

[Calva]: https://calva.io

## Run Mikrobloggeriet locally

In VSCode,

1. Open the command palette,
2. Run **Calva: Start a Project REPL and Connect**
3. For *Project Type*, select **deps.edn**
   (because Mikrobloggeriet uses deps.edn, look for a file called `deps.edn`)
4. For *alias*, select **:dev**

You now have a Clojure REPL!
From this Clojure REPL, we can run any code.
Let's start the server.

In the REPL window, run

```
user> (start!)
```

Don't write the user> part, just write `(start!)`.
`start!` is a function.
By wrapping it in parentheses like `(start!)`, we run it with zero arguments.
`start!` is defined in `src/user.clj`, and is a function we've written ourselves.
`user>` means that you are in the `user` namespace.
`user/start!` is only for local development, the server uses a _different entrypoint_ in production.

You should now see a web browser pop up.
Congratulations!
It works!

## Run the tests

Mikrobloggeriet has unit tests.
To run run all unit tests _once_ in a terminal, running

```
bin/kaocha
```

in a terminal.

To run all the tests automatically each time you save a file, running

```
bin/kaocha --watch
```

in a terminal.

Keeping a terminal open that runs all the tests all the time is a really nice way to know whether the changes you are making are working.
Try it out!
