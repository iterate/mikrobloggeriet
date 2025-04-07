(ns mblog.leik
  "en ekstra leken kohort"
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]))

(defn load-doc [f]
  (when (fs/exists? f)
    (when-let [number (second (re-matches #"(\d+)\.md" (fs/file-name f)))]
      {:doc/slug (str "leik-" number)
       :doc/markdown (slurp (fs/file f))
       :doc/created (-> f fs/creation-time str)
       :doc/cohort [:cohort/slug "leik"]})))

(defn find-docs []
  (->> (fs/list-dir "text/leik")
       (keep load-doc)))

(comment
  (set! *print-namespace-maps* false)
  (find-docs)
  (def f "text/leik/1.md")
  (fs/file-name f)
  (load-doc f)
  (some-> (fs/creation-time "text/leik/1.md")
          str
          (str/split #"T")
          first)
  ;; => "2025-03-19"

  (some-> (fs/creation-time "text/leik/1.md")
          str)
  ;; => "2025-03-19T17:45:20Z"


  )
