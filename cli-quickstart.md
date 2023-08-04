# CLI quickstart

I want to merge `jals` and `olorm` into a single CLI: `mblog`.
That change requires some work.
Untill later, we can keep on using the existing tools:

## Required dependencies

Mikrobloggeriet.no requires some setup:

1. [Babashka] must be installed.
2. [Bbin] must be set.
3. `EDITOR` must be set in your shell.
   Set `export EDITOR=vim` in your `.zshrc` to use Vim, like the default for `git`.

[Babashka]: https://babashka.org/
[Bbin]: https://github.com/babashka/bbin

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
