(ns olorm.cli
  ^{:deprecated true
    :doc "Legacy OLORM CLI, please see README for updated installation instructions."}
  (:require
   [clojure.string :as str]
   [mikrobloggeriet.olorm-cli]))

(defn -main [& args]
  (let [deprecation-warning (str/trim "

WARNING: You are running a legacy installation of the OLORM CLI (deprecated).
Please follow instructions in README.md to install the new OLORM CLI.

")]
    (binding [*out* *err*]
      (println deprecation-warning)
      (println)))
  (apply mikrobloggeriet.olorm-cli/-main args)
  )
