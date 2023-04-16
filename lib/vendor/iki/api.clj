(ns iki.api
  (:require
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [babashka.process]))

;; vendored iki API namespace
;;
;; the idea is to prototype functionality close to the user before "collecting"
;; together in a central artifact.
;;
;; I tried making an IKI library earlier, but it was too early. Too much hassle
;; with versions, too little progress.

(defn markdown->html
  "Converts mardown to html by shelling out to Pandoc"
  [markdown]
  (when (string? markdown)
    (slurp (:out (babashka.process/process "pandoc --from markdown+smart --to html" {:in markdown})))))

(clerk/html
 (markdown->html (str/trim "
### Welcome

This is _it_.
")))

(slurp (:out (babashka.process/process "echo 123123")))
(slurp (:out (babashka.process/process "cat"
                                       {:in "123123"})))

(slurp (:out (babashka.process/process "pandoc --from markdown --to html" {:in "\"fine.\" is this _fine_?"})))

(slurp (:out (babashka.process/process "pandoc --from markdown+smart --to html" {:in "\"fine.\" is this _fine_?"})))
