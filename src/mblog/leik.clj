(ns mblog.leik
  "en ekstra leken kohort"
  (:require
   [babashka.fs :as fs]))

(defn load-doc [f]
  (when-let [number (first (re-matches #"(\d+)\.md" (fs/file-name f)))]
    {:doc/slug (str "leik-" number)
     :doc/markdown (slurp (fs/file f))
     ;;     :doc/created "2099-12-12"
     }
    ))

(defn find-docs []
  (->> (fs/list-dir "text/leik")
      (keep load-doc)))

(comment
  (find-docs)
  (def f "text/leik/1.md")
  (fs/file-name f)
  (load-doc f)
  )
