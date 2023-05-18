(ns mikrobloggeriet.cli.olorm
  (:require [mikrobloggeriet.olorm-cli :as olorm-cli]))

(defn -main [& args]
  (apply mikrobloggeriet.olorm-cli/-main args))
