(ns mikrobloggeriet.teodor.clerk-explore
  (:require [nextjournal.clerk :as clerk]
            [mikrobloggeriet.pandoc :as pandoc]))

(let [markdown
      "% amazing grace

# amazing grace
"
      ]
  (-> markdown
      pandoc/from-markdown
      pandoc/title))
