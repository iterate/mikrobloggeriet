;; # Vendored IKI API
;;
;; We choose to prototype the functionality we need from IKI close to its use
;; before solidification.

(ns iki.api
  (:require
   [clojure.string :as str]
   [babashka.process]))

(defn markdown->html
  "Converts mardown to html by shelling out to Pandoc"
  [markdown]
  (when (string? markdown)
    (slurp (:out (babashka.process/process "pandoc --from markdown+smart --to html" {:in markdown})))))

;; ## Clerk example usage

(when-let [html (requiring-resolve 'nextjournal.clerk/html)]
  (html
   (markdown->html (str/trim "
### Welcome

This is _it_.
"))))
