# For å installere, kopier herfra inn i ~/.zshrc
#
# Endre navn som du vil. Her i denne fila unngår jeg korte navn for å få
# kollisjoner.

function j-macdown () {
  # kjør j-macdown for å lage en jals og redigere i MacDown[1].
  #
  # Kan for eksempel installeres som `j`.
  #
  # [1]: https://macdown.uranusjr.com/
  EDITOR="open -W -a MacDown" jals create
}
