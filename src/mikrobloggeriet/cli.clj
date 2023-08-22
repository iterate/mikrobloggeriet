(ns mikrobloggeriet.cli
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [mikrobloggeriet.cohort :as cohort]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Low-level helpers for managing the config file

(defn- config-file []
  (fs/file (fs/xdg-config-home "mikrobloggeriet") "config.edn"))

(defn- load-config []
  (into (sorted-map)
        (if (fs/exists? (config-file))
          (edn/read-string (slurp (config-file)))
          {})))

(defn- config-get [property]
  (get (load-config) property))

(comment
  (config-get :repo-path))

(defn- config-set [property value]
  (let [config (load-config)]
    (when-not (fs/exists? (config-file))
      (fs/create-dirs (fs/xdg-config-home "mikrobloggeriet")))
    (spit (config-file)
          (with-out-str (pprint (assoc config property value))))))

(comment
  (fs/exists? (config-file))
  (config-get :cohort)
  (config-set :cohort :olorm)
  (config-set :editor "emacs")
  (config-get :editor)
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

(declare subcommands)

(defn mblog-help [_opts]
  (println "Available subcommands:")
  (println)
  (doseq [c subcommands]
    (println (str "  " (str/join " " (:cmds c))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subcommand table

(def subcommands
  [{:cmds ["help"] :fn mblog-help}
   {:cmds ["config"] :fn mblog-config :args->opts [:property :value]}
   {:cmds [] :fn mblog-help}])

(defn -main [& args]
  (cli/dispatch subcommands
                args
                {:coerce {:property :keyword
                          :value :string}
                 :no-keyword-opts true}))
