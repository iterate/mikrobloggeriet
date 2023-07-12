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

(comment
  ;; idea
  ;;
  ;; first - prep
  ;; then - apply
  ;;
  ;; (from Slipset when I asked for feedback on https://play.teod.eu/clojure-let/)

  (do
    (defn create-opts->commands
      "Idea: emit a sequence of commands to be applied"
      [opts]
      (let [defaults {:git true :help false :dry-run false}
            opts (merge defaults opts)
            helptext (str/trim "
Usage:

  $ olorm create [OPTION...]

Allowed options:

  --disable-git-commands  Disable all Git commands. Useful for testing.
  --disable-git-magic     Alias for --disable-git-commands
  --dry-run               Supress side effects and print commands instead
  --help                  Show this helptext.
  --no-git-commands  Disable all Git commands. Useful for testing.
  --no-git-magic          Alias for --disable-git-commands
")]
        (cond (:help opts)
              [{:cmd :println :args helptext}]

              ))

      )


    (defn create-run
      "Idea: execute a list of commands"
      ([commands] (create-run commands {}))
      ([commands overide-handlers]
       (let [handlers (merge {:prn prn
                              :println println
                              :crash (fn [{:keys [command]}]
                                       (println "Error: no handler found for command" command)
                                       (System/exit 1))}
                             overide-handlers)]
         (doseq [{:keys [command args] :as op} commands]
           (prn op)
           (if-let [h (get handlers command)]
             (apply h args)
             ((get handlers :crash) op))))))

    (-> {:help true}
        create-opts->commands
        (create-run {:crash prn})
        )

    ,,, #_ "end of do block")
  ,,, #_ "end of comment block")

(defn olorm-create [{:keys [opts]}]
  (when (or (:help opts) (:h opts))
    (println (str/trim "
Usage:

  $ olorm create [OPTION...]

Allowed options:

  --no-git                Disables all git commands.
  --no-editor             Does not launch $EDITOR to edit files.
                          Also supresses git commit & git push.
  --dry-run               Supress side effects and print commands instead
  --help                  Show this helptext.
"))
    (System/exit 0))
  (let [repo-path (repo-path)
        dispatch (fn [cmd & args]
                   (if (:dry-run opts)
                     (prn `(~cmd ~@args))
                     (apply (resolve cmd) args)))
        disable-git (= (:git opts) false)
        disable-editor (= (:editor opts) false)
        git (:git opts true)       ; By default, git commands are executed
        editor (:editor opts true) ; By default, editor is called
        ]
    (when git
      (dispatch `shell {:dir repo-path} "git pull --ff-only"))
    (let [number (inc (or (->> (olorm/docs {:repo-path repo-path})
                               (map :number)
                               sort
                               last)
                          0))
          doc (olorm/->olorm {:repo-path repo-path :number number})]
      (dispatch `fs/create-dirs (olorm/path doc))
      (let [index-md-path (olorm/index-md-path doc)]
        (dispatch `spit index-md-path (olorm/md-skeleton doc))
        (dispatch `spit (olorm/meta-path doc) (prn-str {:git.user/email (olorm/git-user-email {:repo-path repo-path})
                                                        :doc/created (olorm/today)
                                                        :doc/uuid (olorm/uuid)}))
        (dispatch `shell {:dir repo-path} (System/getenv "EDITOR") index-md-path)
        (when (and git editor)
          (dispatch `shell {:dir repo-path} "git add .")
          (dispatch `shell {:dir repo-path} "git commit -m" (str "olorm-" (:number doc)))
          (dispatch `shell {:dir repo-path} "git pull --rebase") ;; pull & rebase if someone is writing another another microblog entry
          (dispatch `shell {:dir repo-path} "git push")))
      (println (str "Husk å publisere i #mikrobloggeriet-announce på Slack. Feks:"
                    "\n\n"
                    (str "   OLORM-" (:number doc) ": $DIN_TITTEL → https://mikrobloggeriet.no/o/" (:slug doc) "/"))))))

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

(defn olorm-migrate [{:keys [opts]}]
  (when (or (:h opts) (:help opts)
            (not (seq opts)))
    (println (str/trim "
Usage:

  $ olorm migrate [OPTION...]

Allowed options:

  --add-uuids             Add missing UUIDs, conform uuid storage format
  --conform-created       Add a :doc/created attribute for every doc with a created timestamp
  --infer-created         Infer created date from Git history
  --infer-email           Infer email from Git history
"
                         ))
    )
  ;; Add UUIDs for documents missing UUID
  (when (:add-uuids opts)
    (doseq [o (olorm/docs {:repo-path (repo-path)})]
      (let [meta (olorm/load-meta o)
            uuid (or (:doc/uuid meta)
                     (:uuid meta)
                     (olorm/uuid))]
        (olorm/save-meta! (-> meta
                              (dissoc :uuid)
                              (assoc :doc/uuid uuid))))))

  ;; Conform the :doc/created date field
  (when (:conform-created opts)
    (doseq [o (olorm/docs {:repo-path (repo-path)})]
      (let [meta (olorm/load-meta o)
            created (or (:doc/created meta)
                        (:created meta))]
        (when created
          (olorm/save-meta! (-> meta
                                (dissoc :created)
                                (assoc :doc/created created)))))))

  (when (:infer-email opts)
    (doseq [o (olorm/docs {:repo-path (repo-path)})]
      (let [meta (olorm/load-meta o)
            git-user-email (:git.user/email meta)]
        (when-not git-user-email
          (let [candidates
                (->> (shell {:dir (:repo-path o) :out :string}
                            "git log --pretty=\"format:%ae\""
                            (olorm/index-md-path o))
                     :out
                     str/split-lines
                     (remove #{"git@teod.eu" "tingstad@users.noreply.github.com"})
                     dedupe)]
            (when (= (count candidates) 1)
              ;; then it looks good
              (olorm/save-meta! (-> meta
                                    (assoc :git.user/email (first candidates))))))))))

  (when (:infer-created opts)
    (doseq [o (olorm/docs {:repo-path (repo-path)})]
      (let [meta (olorm/load-meta o)
            created (:doc/created meta)]
        (when-not created
          (olorm/save-meta! (-> meta
                                (assoc :doc/created (olorm/infer-created-date o)))))))))

(def subcommands
  [
   {:cmds ["create"]        :fn olorm-create}
   {:cmds ["help"]          :fn olorm-help}
   {:cmds ["repo-path"]     :fn olorm-repo-path}
   {:cmds ["set-repo-path"] :fn olorm-set-repo-path :args->opts [:repo-path]}
   {:cmds ["draw"]          :fn olorm-draw          :args->opts [:pool]}
   {:cmds ["lol"]           :fn lol}
   {:cmds ["migrate"]       :fn olorm-migrate}
   {:cmds []                :fn olorm-help}])

(defn -main [& args]
  (cli/dispatch subcommands args))

(comment
  (map (fn [args]
         [args (cli/dispatch [{:cmds ["create"]
                               :fn (fn [{:keys [opts]}]
                                     opts)}]
                             args)])
       (list ["create"]
             ["create" "--no-git"]
             ["create" "--no-git" "--no-editor"]))
  ;; => ([["create"] {}]
  ;;     [["create" "--no-git"] {:git false}]
  ;;     [["create" "--no-git" "--no-editor"] {:git false, :editor false}])

  )
