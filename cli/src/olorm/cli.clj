(ns olorm.cli
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [babashka.cli :as cli]))

(declare subcommands)

(defn olorm-set-repo-path [{:keys [opts]}]
  (prn 'olorm-set-repo-path))

(defn olorm-help [{:keys [opts]}]
  (prn 'olorm))

(def subcommands
  [{:cmds ["set-repo-path"] :fn olorm-set-repo-path}
   {:cmds []                :fn olorm-help}])

(defn -main [& args]
  (cli/dispatch subcommands args))
