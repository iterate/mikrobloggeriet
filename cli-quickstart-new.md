# CLI quickstart with `mblog` (not `olorm` or `jals`)

This guide gets you started with the new CLI: `mblog`.
`mblog` is currently (per 2023-08-22) work in progress.

## Required dependencies

Mikrobloggeriet.no requires some setup:

1. [Babashka] must be installed.
2. [Bbin] must be set.
3. `EDITOR` must be set in your shell.
   Set `export EDITOR=vim` in your `.zshrc` to use Vim, like the default for `git`.

[Babashka]: https://babashka.org/
[Bbin]: https://github.com/babashka/bbin

## Install `mblog`

To install the CLI, please run the following from this directory:

```shell
bbin install . --as mblog --main-opts '["-m" "mikrobloggeriet.cli"]'
```

## Configure `mblog`

`mblog` requires the following configuration for writing:

1. Repo path: where you have cloned the `mikrobloggeriet` CLI.
2. User cohort: the default cohort you are writing for.
3. Editor: how you want to edit your text.

When both are set, you can run `mblog create` to write a new post.

To configure repo path, user cohort and editor, run:

    mblog config repo-path YOUR_REPO_PATH
    mblog config cohort YOUR_COHORT
    mblog config editor YOUR_EDITOR

### Example: configure `mblog` for the `genai` cohort and writing with Visual Studio Code

First, make sure you have the `code` command line tool installed for editing files with Visual Studio Code from the command line.
See [Launching from the Command line in the official Mac installation][code-docs-setup-mac].

[code-docs-setup-mac]: https://code.visualstudio.com/docs/setup/mac

Then, set your `mblog` configuration.
From the folder you cloned `mikrobloggeriet`, please run:

    mblog config set repo-path .
    mblog config set cohort genai
    mblog config set editor "code --wait --new-window"
