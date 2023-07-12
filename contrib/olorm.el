;;; olorm.el --- Description -*- lexical-binding: t; -*-
;;
;; Copyright (C) 2023 Teodor Heggelund
;;
;; Author: Teodor Heggelund <emacs@teod.eu>
;; Maintainer: Teodor Heggelund <emacs@teod.eu>
;; Created: July 12, 2023
;; Modified: July 12, 2023
;; Version: 0.0.1

;; Emacs-funksjoner for å kjøre OLORM fra Emacs.
;;
;; For å installere anbefaler jeg å kopiere over til din egen
;; Emacs-konfigurasjon. Ellers gjør du konfigurasjonen din avhengig av at du har
;; lastet ned mikrobloggeriet-repoet.

(defun olorm-create ()
  (interactive)
  (shell-command "olorm create --no-editor"))

(provide 'olorm)
;;; olorm.el ends here
