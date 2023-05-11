# OLORM

| term  | definition                                             |
|-------|--------------------------------------------------------|
| olorm | a CLI for microblogging                                |
| olorm | a collection of microblogs                             |
| olorm | Oddmund, Lars and Richard's microblog collective       |
| jals  | Jørgen, Adrian, Lars and Sindre's microblog collective |
| jals  | a CLI for microblogging                                |

Tragically, the "olorm" term is now ambiguous.
But [Cool URIs don't change].
I don't want to break existing links.

The user experience of OLORM and JALS will suffer a bit from this.
I ask you to to bear with me for the sake of the URIs :)

[Cool URIs don't change]: https://www.w3.org/Provider/Style/URI

## CLI prerequisites

In order to install `olorm` or `jals`, please:

1. Clone this repo
2. Install [babashka] and [bbin]
3. Then install `olorm`, `jals` or both.

    ```shell
    bbin install ./olorm-cli
    bbin install ./jals-cli
    ```

You should then be able to invoke the CLI:

    $ olorm -h
    Available subcommands:

      olorm create
      olorm help
      olorm repo-path
      olorm set-repo-path

