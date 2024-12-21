(ns mikrobloggeriet.dev
  (:require
   [babashka.process :as p]))

(defn garden-deploy!
  "Idea: why don't we just deploy from a connected REPL?"
  []
  ;; TODO run the tests
  ;; TDOO check that we're on master
  ;; IN THAT CASE,
  (comment (p/shell "garden deploy")))

(comment ;; s-:
  (garden-deploy!)
  )
