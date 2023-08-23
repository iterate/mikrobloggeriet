# CLI quickstart for `mblog`

This guide gets you started with the new CLI: `mblog`.
`mblog` is currently (per 2023-08-22) work in progress.

## Required dependencies

Mikrobloggeriet.no requires some setup:

1. [Babashka] must be installed.
2. [bbin] must be installed.
3. You must clone this repository.

[Babashka]: https://babashka.org/
[bbin]: https://github.com/babashka/bbin

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

1. Repo path: where you have cloned the `mikrobloggeriet` CLI.
2. User cohort: the default cohort you are writing for.
3. Editor: how you want to edit your text.

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
