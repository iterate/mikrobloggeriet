(ns mikrobloggeriet.cli
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.store :as store]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Low-level helpers for managing the config file

(defn- config-file []
  (fs/file (fs/xdg-config-home "mikrobloggeriet") "config.edn"))

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
        (fs/create-dirs (fs/xdg-config-home "mikrobloggeriet")))
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
  (config-set :repo-path (str (fs/canonicalize ".")))
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subcommands

(defn config-set-repo-path [repo-path]
  (let [repo-path (cli/coerce repo-path :string)]
    (when-let [canonicalized (fs/canonicalize repo-path)]
      (when (fs/exists? canonicalized)
        (config-set :repo-path (str canonicalized))))))

(defn config-set-cohort [cohort]
  (let [cohort (cli/coerce cohort :keyword)]
    (when (contains? cohort/cohorts cohort)
      (config-set :cohort cohort))))

(defn config-set-editor [editor]
  (when (and (string? editor)
             (not (str/blank? editor)))
    (config-set :editor editor)))

(defn mblog-config [opts+args]
  (let [property (:property (:opts opts+args))
        value (:value (:opts opts+args))]
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

(comment
  ;; Changing to the following CLI convention:
  
    ;; mblog config PROPERTY -- reads a property
    ;; mblog config PROPERTY VALUE -- sets a property to a value 

  (let [cohorts (->> store/cohorts
                     (map (fn [c]
                            [(keyword (cohort/slug c))
                             c]))
                     (into (sorted-map)))
        cohort (cohorts (config-get :cohort))
        number (inc (or (->> (store/docs cohort)
                             last
                             doc/number)
                        0))
        doc (doc/from-slug (str (cohort/slug cohort) "-" number))]
    (str (clojure.string/upper-case (doc/slug doc))))
  
  (let [cohort (->> store/cohorts
              (filter (fn [c]
                        (= (name (config-get :cohort))
                           (cohort/slug c)))))]
    cohort)
  
  
  #_(defn md-skeleton [olorm]
    (let [title (or (when (:number olorm) (str "OLORM-" (:number olorm)))
                    (:slug olorm)
                    "OLORM")]
      (str "# " title "\n\n"
           (str/trim "
  <!-- 1. Hva gjør du akkurat nå? -->
  
  <!-- 2. Finner du kvalitet i det? -->
  
  <!-- 3. Hvorfor / hvorfor ikke? -->
  
  <!-- 4. Call to action---hva ønsker du kommentarer på fra de som leser? -->
  "))))

  #_(fn create-opts->commands
    [{:keys [dir git edit]}]
    (assert dir)
    (assert (some? git))
    (assert (some? edit))
    (when edit
      (assert (System/getenv "EDITOR")))
    (let [number (->> (olorm/docs {:repo-path dir})
                      (olorm/next-number))
          doc (olorm/->olorm {:number number :repo-path dir})]
      (concat (when git
                [[:shell {:dir dir} "git pull --ff-only"]])
              [[:create-dirs (str (olorm/path doc))]
               [:spit (str (olorm/index-md-path doc))
                (olorm/md-skeleton doc)]
               [:spit (str (olorm/meta-path doc))
                (prn-str {:git.user/email (olorm/git-user-email {:repo-path dir})
                          :doc/created (olorm/today)
                          :doc/uuid (olorm/uuid)})]]
              (when edit
                [[:shell {:dir dir} (System/getenv "EDITOR") (olorm/index-md-path doc)]])
              (when (and git edit)
                [[:shell {:dir dir} "git add ."]
                 [:shell {:dir dir} "git commit -m" (str "olorm-" (:number doc))]
                 [:shell {:dir dir} "git pull --rebase"]
                 [:shell {:dir dir} "git push"]
                 [:println (str "Husk å publisere i #mikrobloggeriet-announce på Slack. Feks:"
                                "\n\n"
                                (str "   OLORM-" (:number doc)
                                     ": $DIN_TITTEL → https://mikrobloggeriet.no/o/"
                                     (:slug doc) "/"))]]))))

  )

(defn create-opts->commands 
  [{:keys [dir git editor]}]
  (assert dir)
  (assert (some? git))
  (assert (or (nil? editor)
              (string? editor)))
  
  (let [cohorts (->> store/cohorts
                     (map (fn [c]
                            [(keyword (cohort/slug c))
                             c]))
                     (into (sorted-map)))
        cohort (cohorts (config-get :cohort))
        number (inc (or (->> (store/docs cohort)
                             last
                             doc/number)
                        0))
        doc (doc/from-slug (str (cohort/slug cohort) "-" number))] 
    (concat
   ;; git
     (when git
       [[:shell {:dir dir} "git pull --ff-only"]])
   ;; ikke git
   [[:create-dirs (store/doc-folder cohort doc)]
    [:spit
     (store/doc-md-path cohort doc)
     (str "# " (str (clojure.string/upper-case (doc/slug doc)))
          "\n\n<!-- 1. Hva gjør du akkurat nå? -->
           \n\n<!-- 2. Finner du kvalitet i det? -->
           \n\n<!-- 3. Hvorfor / hvorfor ikke? -->
           \n\n<!-- 4. Call to action---hva ønsker du kommentarer på fra de som leser? -->")]
    [:spit
     "/Users/teodorlu/dev/iterate/mikrobloggeriet/o/olorm-35/meta.edn"
     "{:git.user/email \"git@teod.eu\", :doc/created \"2023-08-25\", :doc/uuid \"7da4962d-7506-4c5f-b430-2910af546add\"}\n"]]

     [[:shell {:dir dir} editor
       "/Users/teodorlu/dev/iterate/mikrobloggeriet/o/olorm-35/index.md"]]

   ;; git
     (when git
       [[:shell {:dir dir} "git add ."]
        [:shell {:dir dir} "git commit -m" "olorm-35"]
        [:shell {:dir dir} "git pull --rebase"]
        [:shell {:dir dir} "git push"]])
   ;; ikke git
     [[:println
       "Husk å publisere i #mikrobloggeriet-announce på Slack. Feks:\n\n   OLORM-35: $DIN_TITTEL → https://mikrobloggeriet.no/o/olorm-35/"]]))

  )

(comment
  (create-opts->commands {:dir "/somedidr" :git "somegit" :edit "eidit"})
  )

(defn command->dry-command [command]
  [:prn command])

(declare subcommands)

(defn mblog-help [_opts]
  (println "Available subcommands:")
  (println)
  (doseq [c subcommands]
    (when-not (::experimental c)
      (println (str "  " (str/join " " (:cmds c)))))))

(defn mblog-create [{:keys [opts]}]
  (when (or (:help opts) (:h opts))
    (println (str/trim "
                          Usage:
  
    $ mblog create [OPTION...]
  
  Allowed options:
  
    --no-git   Disables all git commands.
    --no-edit  Do not launch $EDITOR to edit files.
               Also supresses git commit & git push.
    --dry-run  Supress side effects and print commands instead
    --help     Show this helptext.
                        
                        "))
    (System/exit 0))
  (let [command-transform (if (:dry-run opts)
                            command->dry-command
                            identity)]
    (->> {:dir (or (:dir opts) (config-get :repo-path))
          :git (:git opts true)
          :edit (:edit opts true)}
         create-opts->commands
         #_(map command-transform)
         #_execute!))
  )

(defn mblog-links [opts+args]
  (let [format (or (:format (:opts opts+args))
                   :markdown)
        supported-formats #{:markdown}]
    (assert (supported-formats format) (str "Supported formats: " (str/join ", " supported-formats)))
    (doseq [doc (cohort/docs)]
      (let [cohort (get cohort/cohorts (:cohort/id doc))
            href (doc/href cohort doc)
            href-absolute (str "https://mikrobloggeriet.no" href)
            link-title (:doc/slug doc) ;; title could be better --- but would take forever without a cache
            markdown-link (str "[" link-title "](" href-absolute ")")]
        (println markdown-link)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subcommand table
;;
;; Set ::experimental true for experimental commands. Experimental commands are
;; not printed when listing subcommands with `mblog help`.

(def subcommands
  [{:cmds ["help"] :fn mblog-help}
   {:cmds ["config"] :fn mblog-config :args->opts [:property :value]}
   {:cmds ["create"] :fn mblog-create :args->opts [:property :value]}
   {:cmds ["links"] :fn mblog-links ::experimental true}
   {:cmds [] :fn mblog-help}])

(defn -main [& args]
  (cli/dispatch subcommands
                args
                {:coerce {:property :keyword
                          :value :string
                          :format :keyword}
                 :no-keyword-opts true}))
