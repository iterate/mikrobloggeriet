(ns mikrobloggeriet.cli
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [babashka.process :as process :refer [shell]]
   [clojure.edn :as edn]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.store :as store]))

;; Ensure we load babashka libraries from the classpath, not from built-in
;; babashka versions. This lets us avoid bugs like
;; https://github.com/iterate/mikrobloggeriet/issues/56 and
;; https://github.com/iterate/mikrobloggeriet/issues/53.
#_{:clj-kondo/ignore [:duplicate-require]}
(require 'babashka.fs :reload)
#_{:clj-kondo/ignore [:duplicate-require]}
(require 'babashka.cli :reload)
#_{:clj-kondo/ignore [:duplicate-require]}
(require 'babashka.process :reload)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Low-level helpers for managing the config file

(defn- config-file []
  (fs/file (fs/xdg-config-home) "mikrobloggeriet" "config.edn"))

(defn- load-config []
  (into (sorted-map)
        (if (fs/exists? (config-file))
          (edn/read-string (slurp (config-file)))
          {})))

(defn- wipe-config []
  (fs/delete-if-exists (config-file)))

(defn- config-get [property]
  (get (load-config) property))

(comment
  (config-get :repo-path))

(defn- config-set [property value]
  (when (= value
           (try
             (-> value pr-str edn/read-string)
             (catch java.lang.RuntimeException _
               ::cannot-roundtrip)))
    (let [config (load-config)]
      (when-not (fs/exists? (config-file))
        (fs/create-dirs (fs/file (fs/xdg-config-home) "mikrobloggeriet")))
      (spit (config-file)
            (with-out-str (pprint (assoc config property value)))))))

(comment
  (wipe-config)

  (fs/exists? (config-file))
  (config-get :cohort)
  (config-set :cohort :olorm)
  (config-set :editor "emacs")
  (config-get :editor)
  (config-get :repo-path)
  (config-set :repo-path (fs/canonicalize "."))
  (config-set :repo-path (str (fs/canonicalize "."))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subcommands

(defn config-set-repo-path [repo-path]
  (let [repo-path (cli/coerce repo-path :string)]
    (when-let [canonicalized (fs/canonicalize repo-path)]
      (when (fs/exists? canonicalized)
        (config-set :repo-path (str canonicalized))))))

(defn config-set-cohort [cohort]
  (let [cohort (cli/coerce cohort :keyword)]
    (if (contains? store/cohorts cohort)
      (config-set :cohort cohort)
      (do
        (println (str "Error: cohort " cohort " not found."))
        (println (str "Valid cohorts are: "))
        (println)
        (doseq [cohort-id (keys store/cohorts)]
          (println (str "    " cohort-id)))
        (System/exit 1)))))

(defn config-set-editor [editor]
  (when (and (string? editor)
             (not (str/blank? editor)))
    (config-set :editor editor)))

(defn mblog-config [opts+args]
  (let [property (:property (:opts opts+args))
        value (:value (:opts opts+args))]
    (when (and property
               (or (:help (:opts opts+args))
                   (:h (:opts opts+args))))
      (println (str/trim "
Get current cohort:

    mblog config cohort

Set current cohort:

    mblog config cohort COHORT
"))
      (println)
      (println "Known cohorts:")
      (println)
      (doseq [[c _] store/cohorts]
        (println (str "    " c)))
      (System/exit 0))
    (when (or (:help (:opts opts+args))
              (:h (:opts opts+args)))
      (println (str/trim "
Usage:

    mblog config [PROPERTY [VALUE]]

There are three allowed variants.

    `mblog config`: print your whole config.

    `mblog config PROPERTY`: read PROPERTY from your config

    `mblog config PROPERTY VALUE`: set PROPERTY to VALUE in your config.

Supported values for PROPERTY:

- `cohort`: Your cohort.
- `editor`: Your editor for writing documents.
- `repo-path`: The location of the Mikrobloggeriet CLI on your computer. "))
      (System/exit 0))
    (when (and (not property) (not value))
      ;; No args
      (clojure.pprint/pprint (load-config))
      (System/exit 0))
    (when (#{:repo-path :editor :cohort} property)
      (if (nil? value)
        ;; get
        (when-let [v (config-get property)]
          (println v))
        ;; set
        (case property
          :repo-path (config-set-repo-path value)
          :editor (config-set-editor value)
          :cohort (config-set-cohort value))))))

(defn md-skeleton [doc]
  (str "# " (str/upper-case (doc/slug doc)) "\n\n"
       (str/trim "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->

<!-- 4. Call to action---hva ønsker du kommentarer på fra de som leser? -->")))

(defn- git-user-email [dir]
  (str/trim (:out (shell {:out :string :dir dir} "git config user.email"))))

(defn- today []
  (.format (java.time.LocalDateTime/now)
           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(defn- uuid []
  (str (java.util.UUID/randomUUID)))

(defn create-opts->commands
  "Creates commands to shelling out for creating new mblog post.
    Takes in a map of `opts`. Supported options in `opts`:
  
    | key           | description |
    | --------------|-------------|
    | `dir`         | The directory where shelling out happens.
    | `git`         | Boolean specifying whether to create shelling out to git commands or not.
    | `editor`      | Specify editor to open when shell out.
    | `cohort-id`   | Keyword for the specifying cohort.
  
    Example:
    ```clojure
    (def sample-opts
     {:dir \".\"
      :git true
      :editor \"vim\"
      :cohort-id :olorm
      :git.user/email \"user@example.com\"})

    (create-opts->commands sample-opts) 
    ```"
  [opts]
  (let [{:keys [dir git editor cohort-id]} opts
        git-user-email (:git.user/email opts)]
    (assert dir)
    (assert (some? git))
    (assert (or (nil? editor)
                (string? editor)))
    (assert git-user-email)
    (assert cohort-id)

    (let [cohort (-> (store/cohorts cohort-id)
                     (cohort/set-repo-path dir))
          doc (store/next-doc cohort)
          proposed-title (str/upper-case (doc/slug doc))]
      (concat (when git
                [[:shell {:dir dir} "git pull --ff-only"]])
              [[:create-dirs (str (store/doc-folder cohort doc))]
               [:spit
                (str (store/doc-md-path cohort doc))
                ((cohort/index-md-template cohort) {:title proposed-title})]
               (let [doc-meta (cond-> {:git.user/email git-user-email
                                       :doc/created (today)
                                       :doc/uuid (uuid)}
                                (:draft opts) (assoc :doc/state :draft))]
                 [:spit (str (store/doc-meta-path cohort doc)) (with-out-str (pprint doc-meta))])]
              (when editor
                [[:shell {:dir dir} editor (str (store/doc-md-path cohort doc))]])
              (when (and git editor)
                [[:shell {:dir dir} "git add ."]
                 [:shell {:dir dir} "git commit -m" (str (doc/slug doc))]
                 [:shell {:dir dir} "git pull --rebase"]
                 [:shell {:dir dir} "git push"]
                 [:println (str "Husk å publisere i #mikrobloggeriet-announce på Slack. Feks:"
                                "\n\n   "
                                (str (str/upper-case (doc/slug doc))
                                     ": $DIN_TITTEL → https://mikrobloggeriet.no"
                                     (store/doc-href cohort doc)))]])))))

(defn execute!
  "Execute a sequence of commands."
  [commands]
  (doseq [[cmd & args] commands]
    (case cmd
      :create-dirs (apply fs/create-dirs args)
      :println (apply println args)
      :prn (apply prn args)
      :shell (apply shell args)
      :spit (apply spit args))))

(comment
  (def sample-opts
    {:dir "."
     :git true
     :editor "vim"
     :cohort-id :oj
     :git.user/email "user@example.com"})

  (create-opts->commands sample-opts))

(defn command->dry-command [command]
  [:prn command])

(declare subcommands)

(defn mblog-help [_opts]
  (println "Available subcommands:")
  (println)
  (doseq [c subcommands]
    (when-not (::experimental c)
      (println (str "  " (str/join " " (:cmds c)))))))

(defn ^:private configured-properties? [config-keys]
  (every? config-get config-keys))

(defn ^:private config-error-message [config-keys]
  (str/join "\n"
            (->> config-keys
                 (filter (fn [property]
                           (not (some? (config-get property)))))
                 (map (fn [property]
                        (str "Property missing: " property ". To learn how to add it, run `mblog config -h`."))))))

(comment
  (configured-properties? '(:cohort))
  (config-error-message '(:cohort :editor :repo-path)))

(defn mblog-create [{:keys [opts]}]
  (when (or (:help opts) (:h opts))
    (println (str/trim "
Usage:

  $ mblog create [OPTION...]

Allowed options:

  --no-git   Disables all git commands.
  --no-edit  Do not launch $EDITOR to edit files.
             Also supresses git commit & git push.
  --dry-run  Supress side effects and print commands instead.
  --help     Show this helptext.
  --draft    EXPERIMENTAL! (may not work, stop working and/or change behavior)
"))
    (System/exit 0))
  (let [default-opts {:edit true}
        opts (merge default-opts opts)
        opts (let [edit? (:edit opts true) ;; by default, we shell out to the user's editor to write.
                   git? (cond
                          (= (:git opts) true) true ;; if explicitly set in a CLI option, do what the user says.
                          (not edit?) false         ;; otherwise, if the user has disabled editing, also disable Git.
                          :else (:git opts true)    ;; otherwise, take the CLI arg, and default to true.
                          )]
               (assoc opts
                      :edit edit?
                      :git git?))
        required-properties (if (:edit opts)
                              #{:editor :cohort :repo-path}
                              #{:cohort :repo-path})]
    (when-not (configured-properties? required-properties)
      (println (config-error-message required-properties))
      (System/exit 1))
    (when (:edit opts)
      (let [editor (config-get :editor)]
        (when-not (fs/which (first (process/tokenize editor)))
          (println "Error: editor not found.")
          (println)
          (println (str "    " editor))
          (println)
          (println "was not found on your system. You must be able to use your configured editor to
edit files from a terminal. For example:")
          (println)
          (println (str "    $ " editor " yourfile.txt"))
          (println)
          (println "If that command crashes," editor "can't be used as an editor.")
          (println "To learn how to configure your editor, run `mblog config -h`.")
          (System/exit 1))))
    (let [command-transform (if (:dry-run opts)
                              command->dry-command
                              identity)
          dir (config-get :repo-path)
          create-opts {:dir dir
                       :git (:git opts true)
                       :editor (when (not= false (:edit opts))
                                 (config-get :editor))
                       :git.user/email (git-user-email dir)
                       :cohort-id (config-get :cohort)
                       :draft (or (:draft opts) false)}]
      (->> create-opts
           create-opts->commands
           (map command-transform)
           execute!))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subcommand table
;;
;; Set ::experimental true for experimental commands. Experimental commands are
;; not printed when listing subcommands with `mblog help`.

(def subcommands
  [{:cmds ["help"] :fn mblog-help}
   {:cmds ["config"] :fn mblog-config :args->opts [:property :value]}
   {:cmds ["create"] :fn mblog-create :args->opts [:property :value]}
   {:cmds [] :fn mblog-help}])

(defn -main [& args]
  (cli/dispatch subcommands
                args
                {:coerce {:property :keyword
                          :value :string
                          :format :keyword}
                 :no-keyword-opts true}))
