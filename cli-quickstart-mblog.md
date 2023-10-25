# CLI quickstart for `mblog`

This guide gets you started with the new CLI: `mblog`.
Per `2023-09-15`:

1. `mblog` is the recommended CLI for writing documents,
2. `olorm` and `jals` are deprecated, but still supported.
   To install `olorm` or `jals`, please read the [old `olorm` and `jals` CLI Quickstart].

[old `olorm` and `jals` CLI Quickstart]: cli-quickstart.md

## Required dependencies

Mikrobloggeriet.no requires some setup:

1. Java must be installed.
   If you use homebrew to install Java on a mac, you may have to run an additional command to select the active java version.
   You have a working java installation if you can run `java --help` and `javac --help` in a terminal.

2. [Babashka] must be installed.
   You have a working Babashka installation if you can run `bb --help` in a terminal.

3. [bbin] must be installed and added to your PATH.
   Running `bbin` in a terminal should show something like this:

    ```shell
    $ bbin
    Usage: bbin <command>

    bbin install    Install a script
    bbin uninstall  Remove a script
    bbin ls         List installed scripts
    bbin bin        Display bbin bin folder
    bbin version    Display bbin version
    bbin help       Display bbin help
    ```

4. You must clone this repository.

[Babashka]: https://babashka.org/
[bbin]: https://github.com/babashka/bbin

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

## Install `mblog`

To install the CLI with bbin, run the following command from inside the directory, meaning where you cloned down the repository:

```shell
bbin install . --as mblog --main-opts '["-m" "mikrobloggeriet.cli"]'
```

If the installation succeeds, you'll see output like this in your terminal:

```
{:coords
 #:bbin{:url "file:///Users/teodorlu/dev/iterate/mikrobloggeriet"}}
```

## Configure `mblog`

`mblog` requires the following configuration for writing:

1. Repo path: where you have cloned the `mikrobloggeriet` repository.
2. User cohort: the default cohort you are writing for.
3. Editor: which application you want to use to write content.

To configure repo path, user cohort and editor, run:

    mblog config repo-path YOUR_REPO_PATH
    mblog config cohort YOUR_COHORT
    mblog config editor YOUR_EDITOR

When repo-path, cohort and editor are set, you can run `mblog create` to write a new post.

### Example: configure `mblog` for the `genai` cohort and writing with Visual Studio Code

First, make sure you have the `code` command line tool installed for editing files with Visual Studio Code from the command line.
See [Launching from the Command line in the official Mac installation][code-docs-setup-mac].

[code-docs-setup-mac]: https://code.visualstudio.com/docs/setup/mac

Then, set your `mblog` configuration.
From the folder you cloned `mikrobloggeriet`, run:

    mblog config repo-path .
    mblog config cohort genai
    mblog config editor "code --wait --new-window"
