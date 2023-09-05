# CLI quickstart

I want to merge `jals` and `olorm` into a single CLI: `mblog`.
That change requires some work.
Until later, we can keep on using the existing tools:

## Required dependencies

Mikrobloggeriet.no requires some setup:

1. Java must be installed (see below)
2. [Babashka] must be installed.
3. [Bbin] must be installed
4. `EDITOR` must be set in your shell.
   Set `export EDITOR=vim` in your `.zshrc` to use Vim, like the default for `git`.

[Babashka]: https://babashka.org/
[Bbin]: https://github.com/babashka/bbin

## Install and setup Java

### Installing Java on Linux

Please install your Java Development Kit (JDK) of choice with your package manager.

### Installing Java on Mac

You can install Java however you like.

Here's one example on how to do it:

```shell
brew install openjdk
```

You can install multiple Java versions at the same time on Mac.
To choose the java version you just installed, run:

```shell
sudo ln -sfn /usr/local/opt/openjdk/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk.jdk
```

## Install `olorm`

1. First, install the CLI.
   In this folder, please run:

    ```shell
    bbin install . --as olorm --main-opts '["-m" "mikrobloggeriet.olorm-cli"]'
    ```

2. Then, configure the `mikrobloggeriet` repo path.
   This lets you use the CLI from any folder on your computer.
   From this folder, run:

    ```shell
    olorm set-repo-path .
    ```

## Install `jals`

1. First, install the CLI.
   In this folder, please run:

    ```shell
    bbin install . --as jals --main-opts '["-m" "mikrobloggeriet.jals-cli"]'
    ```

2. Then, configure the CLI repo path.
   This lets you use the CLI from any folder on your computer.
   From this folder, run:

    ```shell
    jals set-repo-path .
    ```
