#!/bin/sh
set -e


help() {
    cat <<-EOF
	usage: mblog.sh create COHORT

	example:
	    mblog.sh create text/luke
	EOF
}

create() {
    if [ $# -gt 1 ]; then
        withusage fail 'too many arguments\n'
    fi
    path="${1%/}" # remove any trailing /
    if ! [ -d "$path" ]; then
        withusage fail 'cohort "%s" not found\n' "$1"
    fi
    IFS=- read -r name last <<-EOF
		$(find "$path" -type d -name '*[0-9]' -prune -print \
            | sed 's,.*/,,' | sort -t - -k 2 -n | tail -n1)
	EOF
    [ -n "$name" ] || name=$(basename "$path")
    next=$(( ${last:-0} + 1 ))
    dir="$path/$name-$next"
    dryrun mkdir "$dir"
    dryrun mdfile "$dir/index.md"
    dryrun ednfile "$dir/meta.edn" \
        "$(date +%Y-%m-%d)" \
        "$(uuid4)" \
        "$(git config user.email)"
    printf 'created %s\n'  "$dir/index.md"
    [ -z "$EDITOR" ] || "$EDITOR" "$dir/index.md"
}

mdfile() {
    cat <<-EOF >"$1"
		<!-- Hva gjør du akkurat nå? -->

		<!-- Finner du kvalitet i det? -->

		<!-- Hvorfor / hvorfor ikke? -->

		<!-- Call to action—hva ønsker du kommentarer på fra de som leser? -->

	EOF
}

ednfile() {
    cat <<-EOF >"$1"
		{:doc/created "$2",
		 :doc/uuid "$3",
		 :git.user/email "$4"}
	EOF
}

uuid4() {
    awk 'BEGIN {
        srand()
        print f(8) "-" f(4) "-4" f(3) "-" f(4) "-" f(12)
    }
    function f(len) {
        r = ""
        while (len-- > 0) {
            n = int(rand() * 16)
            r = r sprintf("%c", (n > 9 ? n+97 : n+48))
        }
        return r
    }'
}

fail() {
    [ -t 1 ] && notty=false || notty=true
    $notty || printf '\033[31m' # red
    # shellcheck disable=SC2059 # be careful with %
    printf >&2 "$@"
    $notty || printf '\033[m' # reset
    return 1
}

withusage() {
    rc=0
    if ! "$@"; then
        rc=$?
    fi
    help
    return $rc
}

dryrun() {
    if [ -n "$DRYRUN" ]; then
        set -- echo "$@"
    fi
    "$@"
}

case "$1" in
    create|help|uuid4)
        "$@"
        ;;
    "")
        help
        exit 1
        ;;
    *)
        withusage fail 'unknown argument: %s\n' "$1"
        ;;
esac

