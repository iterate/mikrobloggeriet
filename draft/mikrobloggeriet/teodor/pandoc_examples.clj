(ns mikrobloggeriet.teodor.pandoc-examples
  (:require [mikrobloggeriet.pandoc :as pandoc]))

(pandoc/from-markdown "# olorm 11\n\nhei på deg")
