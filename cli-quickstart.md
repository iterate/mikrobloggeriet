# old `olorm` and `jals` CLI quickstart

**Status**:
Per 2023-09-15, `olorm` and `jals` are deprecated in favor of `mblog`.
New users are recommended to read the [New CLI Quickstart](cli-quickstart-mblog.md) to get started.

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

### Installing Java on Mac with Homebrew

As long as `java -version` prints a Java version, you have a working Java installation.

To install java with Homebrew:

```shell
brew install openjdk
```

Multiple java versions can be installed at the same time.
You need to select one.
Read the Homebrew package docs for an explanation.
You'll probably run something like this:

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
