# For å installere, kopier herfra inn i ~/.zshrc.

function j-macdown () {
  # kjør j-macdown for å lage en jals og redigere i MacDown[1].
  #
  # Kan for eksempel installeres som `j`.
  #
  # [1]: https://macdown.uranusjr.com/
  EDITOR="open -W -a MacDown" jals create
}
