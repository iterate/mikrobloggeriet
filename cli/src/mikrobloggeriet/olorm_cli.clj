;; OLORM CLI

(ns mikrobloggeriet.olorm-cli
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [mikrobloggeriet.olorm :as olorm]
   [babashka.process :refer [shell]]))

(defn config-folder [] (str (fs/xdg-config-home) "/olorm"))
(defn config-file [] (str (config-folder) "/config.edn"))

(defn repo-path []
  (-> (config-file)
      slurp
      edn/read-string
      :repo-path))

(declare subcommands)

(defn olorm-help [{:keys [_opts]}]
  (println "Available subcommands:")
  (println)
  (doseq [{:keys [cmds]} subcommands]
    (when (and (seq cmds) (not= cmds ["lol"]))
      (println (str "  olorm " (str/join " " cmds))))))

(defn olorm-set-repo-path [{:keys [opts]}]
  (let [repo-path (:repo-path opts)]
    (when-not repo-path
      (println (str/trim "
Usage:

    olorm set-repo-path REPO_PATH

REPO_PATH is the path to the OLORM repo. The CLI can be used from any folder on
your system, so we need to know where to find OLORM pages.
"))
      (System/exit 1))
    (let [repo-path (fs/absolutize repo-path)]
      (fs/create-dirs (config-folder))
      (spit (config-file) (prn-str {:repo-path (str repo-path)})))))

(defn olorm-repo-path [{:keys [_opts]}]
  (if (fs/exists? (config-file))
    (println (repo-path))
    (do
      (println "Error: config file not found")
      (System/exit 1))))

(defn olorm-create [{:keys [opts]}]
  (when (or (:help opts) (:h opts))
    (println (str/trim "
Usage:

  $ olorm create [OPTION...]

Allowed options:

  --disable-git-magic  Disable running any Git commands. Useful for testing.
  --dry-run            Supress side effects and print commands instead
  --help               Show this helptext.
  --no-git-magic       Alias for --disable-git-magic
"))
    (System/exit 0))
  (let [repo-path (repo-path)
        dispatch (fn [cmd & args]
                   (if (:dry-run opts)
                     (prn `(~cmd ~@args))
                     (apply (resolve cmd) args)))]
    (when-not (or (:no-git-magic opts) (:disable-git-magic opts))
      (dispatch `shell {:dir repo-path} "git pull --rebase"))
    (let [next-number (inc (or (->> (olorm/olorms {:repo-path repo-path}) (map :number) sort last)
                               0))
          doc (olorm/->olorm {:repo-path repo-path :number next-number})
          next-olorm-dir (olorm/path doc)]
      (dispatch `fs/create-dirs next-olorm-dir)
      (let [next-index-md (olorm/index-md-path doc)]
        (dispatch `spit next-index-md (olorm/md-skeleton doc))
        (dispatch `spit (olorm/meta-path doc) (prn-str {:git.user/email (olorm/git-user-email {:repo-path repo-path})
                                                          :doc/created (olorm/today)
                                                          :doc/uuid (olorm/uuid)}))
        (dispatch `shell {:dir repo-path} (System/getenv "EDITOR") next-index-md)
        (when-not (:disable-git-magic opts)
          (dispatch `shell {:dir repo-path} "git add .")
          (dispatch `shell {:dir repo-path} "git commit -m" (str "olorm-" (:number doc)))
          (dispatch `shell {:dir repo-path} "git push")))
      (let [olorm-announce-nudge (str "Husk å publisere i #mikrobloggeriet-announce på Slack. Feks:"
                                      "\n\n"
                                      (str "   OLORM-" (:number doc) ": $DIN_TITTEL → https://mikrobloggeriet.no/o/" (:slug doc) "/"))]
        (println olorm-announce-nudge)))))

(defn lol [{:keys [opts]}]
  (let [eval-or-show-work (fn [form]
                             (if (:dry-run opts)
                               (prn form)
                               (eval form)))]
    (eval-or-show-work `(prn "trolololo"))
    (eval-or-show-work `(shell "ls" ".."))))

(defn olorm-draw [{:keys [opts]}]
  (let [pool (:pool opts)]
    (when (or (:h opts)
              (:help opts)
              (not pool))
      (println (str/trim "
Usage:

  $ olorm draw POOL

POOL is a string that can contain the first letters of the OLORM authors.
Example usage:

  $ olorm draw olr
  Richard
"
                         ))
      (if (or (:h opts) (:help opts))
        (System/exit 0)
        (System/exit 1)))
    (prn
     (get (zipmap "olr" '(oddmund lars richard))
          (rand-nth pool)))))

(def subcommands
  [
   {:cmds ["create"]        :fn olorm-create}
   {:cmds ["help"]          :fn olorm-help}
   {:cmds ["repo-path"]     :fn olorm-repo-path}
   {:cmds ["set-repo-path"] :fn olorm-set-repo-path :args->opts [:repo-path]}
   {:cmds ["draw"]          :fn olorm-draw          :args->opts [:pool]}
   {:cmds ["lol"]           :fn lol}
   {:cmds []                :fn olorm-help}])

(defn -main [& args]
  (cli/dispatch subcommands args))
