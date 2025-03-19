(ns mblog.leik
  "en ekstra leken kohort"
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]))

(defn created-date [f]
  ;; A "simplest possible" approach to when a file was created. If this doesn't
  ;; work out, we can use the Git log.
  (some-> (fs/creation-time f)
          str (str/split #"T") first))

(defn load-doc [f]
  (when (fs/exists? f)
    (when-let [number (second (re-matches #"(\d+)\.md" (fs/file-name f)))]
      {:doc/slug (str "leik-" number)
       :doc/markdown (slurp (fs/file f))
       :doc/created (created-date f)})))

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
  )
