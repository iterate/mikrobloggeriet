#!/bin/sh
set -e
# POSIX Bourne Shell to support as many clients as possible


help() {
    cat <<-EOF
	usage: mblog.sh create COHORT

	example:
	    mblog.sh create text/luke
	EOF
}

create() {
    [ $# -gt 0 ] || withusage fail 'too few arguments\n'
    [ $# -lt 2 ] || withusage fail 'too many arguments\n'
    path="${1%/}" # remove any trailing /
    [ -d "$path" ] || withusage fail 'cohort "%s" not found\n' "$1"

    IFS=- read -r name last <<-EOF
		$(find "$path" -type d -name '*[0-9]' -prune -print \
            | sed 's,.*/,,' | sort -t - -k 2 -n | tail -n1)
	EOF
    [ -n "$name" ] || name=$(basename "$path")
    next=$(( ${last:-0} + 1 ))

    dir="$path/$name-$next"

    dryrun mkdir "$dir"
    mdfile | tofile "$dir/index.md"
    ednfile \
        "$(date +%Y-%m-%d)" \
        "$(uuid4)" \
        "$(git config user.email)" \
        | tofile "$dir/meta.edn"

    [ -n "$DRYRUN" ] || printf 'created %s\n'  "$dir/index.md"
    [ -n "$DRYRUN" ] || [ -z "$EDITOR" ] || "$EDITOR" "$dir/index.md"
}

mdfile() {
    cat <<-EOF
		<!-- Hva gjør du akkurat nå? -->

		<!-- Finner du kvalitet i det? -->

		<!-- Hvorfor / hvorfor ikke? -->

		<!-- Call to action—hva ønsker du kommentarer på fra de som leser? -->

	EOF
}

ednfile() {
    cat <<-EOF
		{:doc/created "$1",
		 :doc/uuid "$2",
		 :git.user/email "$3"}
	EOF
}

uuid4() {
    awk 'BEGIN {
        srand()
        variant = substr("89ab", 1 + int(rand() * 4), 1)
        print f(8) "-" f(4) "-4" f(3) "-" variant f(3) "-" f(12)
    }
    function f(len) {
        r = ""
        while (len-- > 0) {
            n = int(rand() * 16)
            r = r sprintf("%c", (n > 9 ? n+87 : n+48))
        }
        return r
    }'
}

fail() {
    [ -t 1 ] && notty=false || notty=true
    {
    $notty || printf '\033[31m' # red
    # shellcheck disable=SC2059 # be careful with %
    printf "$@"
    $notty || printf '\033[m' # reset
    } >&2
    return 1
}

withusage() {
    rc=0
    "$@" || rc=$?
    help
    return $rc
}


tofile() {
    if [ -n "$DRYRUN" ]; then
        printf '> %s\n' "$1"
        cat
    else
        cat > "$1"
    fi
}

dryrun() {
    if [ -n "$DRYRUN" ]; then
        set -- echo "$@"
    fi
    "$@"
}

test() {
    # The variant field consists of a variable number of
    # the most significant bits of octet 8 of the UUID. —RFC 4122
    # 1 0 _ _ => 1 0 0 0 .. 1 0 1 1 => 8..11 => [89ab]
    pattern='^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$'
    for i in $(seq 20); do
        value=$(uuid4)
        printf %s "$value" | grep -Eq "$pattern" \
            || fail 'uuid %s did not match pattern %s\n' "$value" "$pattern"
    done
    echo OK
}

# shellcheck disable=SC2086 # integers only
seq() { # [first] last
    last=${2:-$1}
    if [ $# -gt 1 ]; then i=$1; else i=1; fi
    while [ $i -le $last ]; do
        printf '%d\n' $i
        i=$((i + 1))
    done
}

case "$1" in
    create|help|uuid4|test)
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

