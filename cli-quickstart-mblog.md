# CLI quickstart for `mblog`

This guide gets you started with `mblog`, a CLI for writing on Mikrobloggeriet.
Per `2024-01-28`, you have two options for writing on Mikrobloggeriet.

- `mblog` is written in Clojure, mostly by Teodor and Olav
- `mblog.sh` is written in Bourne shell by Richard.

`mblog.sh` requires no setup, and has no configuration.
`mblog` requires several installation steps, and has configuration.

## Required dependencies

Mikrobloggeriet.no requires some setup:

1. Java must be installed.
   If you use homebrew to install Java on a mac, you may have to run an additional command to select the active java version.
   You have a working java installation if you can run `java --help` and `javac --help` in a terminal.

2. [Babashka] must be installed.
   You have a working Babashka installation if you can run `bb --help` in a terminal.
   
   With Homebrew:
   
   ```
   brew install borkdude/brew/babashka
   ```

3. [bbin] must be installed and added to your PATH.
   With Homebrew:
   
   ```
   brew install borkdude/brew/bbin
   ```
   
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

### Example: configure `mblog` for the `genai` cohort and writing with Visual Studio Code

First, make sure you have the `code` command line tool installed for editing files with Visual Studio Code from the command line.
See [Launching from the Command line in the official Mac installation][code-docs-setup-mac].

[code-docs-setup-mac]: https://code.visualstudio.com/docs/setup/mac

Then, set your `mblog` configuration.
From the folder you cloned `mikrobloggeriet`, run:

    mblog config repo-path .
    mblog config cohort genai
    mblog config editor "code --wait --new-window"

## Create and publish a doc
When repo path, cohort and editor are set, you can run `mblog create` to write a new post.

`mblog create` creates the neccessary files, opens your editor, where you can write your blog post. 
Blog posts on mikrobryggeriet are written in __markdown__. After you save and close your editor, `mblog create` automatically pushes your blog post to Github and live on [mikrobloggeriet.no].

Finally, you will see a prompt in you terminal to post you blog post in the #mikrobloggeriet-announce Slack channel to promote discussion.

If you want to do the __git__ operations yourself, you can use the command `mblog create --no-git` to create the files and open the editor. If you do this, make sure you are up to date with the lastest changes in the [Github repository].

[mikrobloggeriet.no]: https://mikrobloggeriet.no
[Github repository]: https://github.com/iterate/mikrobloggeriet/
