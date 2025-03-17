# Mikrobloggeriet

https://mikrobloggeriet.no/

_A collection of microblogs—written in Norwegian._

Vocabulary:

| term            | type            | definition                                        |
|-----------------|-----------------|---------------------------------------------------|
| cohort          | abstraction     | a collection of people writing together           |
| microblog       |                 | alias for "cohort"                                |
| doc             | abstraction     | a microblog entry                                 |
| jals            | cohort          | Jørgen, Adrian, Lars and Sindre's cohort          |
| kiel            | cohort          | about artificial intetelligence and real learning |
| luke            | cohort          | the 2023 christmas calendar initiative            |
| mblog           | CLI application | second generation Mikrobloggeriet CLI             |
| mblog.sh        | CLI application | third generation Mikrobloggeriet CLI              |
| mikrobloggeriet | a web site      | you're looking at it!                             |
| oj              | cohort          | fresh opinions from recent Iterate hires          |
| olorm           | cohort          | Oddmund, Lars and Richard's cohort                |
| urlog           | cohort          | collection of weird and/or interesting web sites  |
| vakt            | cohort          | about the making of  Mikrobloggeriet              |

## Publishing text on Mikrobloggeriet!

Text on Mikrobloggeriet is written in Markdown.
To create a new Markdown text file and a metadata file, you can use `mblog.sh`.
See [CLI quickstart for `mblog.sh`], by Richard.

## Developing Mikrobloggeriet locally!

You have two options, depending on who you are:

| guide                            | target audience               |
|----------------------------------|-------------------------------|
| [CONTRIBUTING.md]                | Any developer, any system     |
| [clojure-quickstart-homebrew.md] | Uses Mac, Homebrew and VSCode |

[clojure-quickstart-homebrew.md]: clojure-quickstart-homebrew.md
[CONTRIBUTING.md]: CONTRIBUTING.md

## Get production access

Mikrobloggeriet runs on Application Garden ([docs.apps.garden](https://docs.apps.garden/).
To deploy Mikrobloggeriet, you need a user account.

1. Get an Application Garden user by signing up on [application.garden/signup](https://application.garden/signup)

2. Ask Olav or Teodor to add you to the `mikrobloggeriet` group on Application Garden.

3. Install the `garden` CLI.

You now can now control the production environment.
Examples:

    # Deploy directly, bypass the tests
    garden deploy

    # Connect from your computer to a production REPL to change production behavior live
    garden repl

    # Browse production file system
    garden sftp

    # View what else you can do
    garden --help

## Deploying Mikrobloggeriet!

Run the tests, then deploy if tests are green:

    bb deploy

## Contributors

(in approximate chronological order)

- Teodor (text and code)
- Oddmund (text and code)
- Lars B (text)
- Richard (text and code)
- Jørgen (text)
- Adrian (text and code)
- Lars BJ (text)
- Sindre (text)
- Johan (text and code)
- Olav (text, code, project management and system design)
- Håvard (text)
- Julian (text and interaction design)
- Finn (text)
- Thusan (text)
- Kjersti (text)
- Rasmus (text)
- Pernille (text)
- Camilla (text)
- Ole Jacob (text)
- Ella (text)
- Rune (text)
- Anders (text)
- Neno (text, interface design, interaction design and code)

Please add your name to the list if your name should be on the list.
