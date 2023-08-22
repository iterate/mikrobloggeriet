(ns mikrobloggeriet.cli
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]))

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
;; Subcommand handlers

(defn mblog-set-repo-path [opts+args]
  (when-let [repo-path (:repo-path (:opts opts+args))]
    (when (fs/exists? repo-path)
      (let [canonicalized (str (fs/canonicalize repo-path))]
        (config-set :repo-path canonicalized)))))

(defn mblog-repo-path [_opts+args]
  (when-let [repo-path (config-get :repo-path)]
    (println repo-path)))

(defn mblog-set-cohort [opts+args]
  (when-let [cohort (:cohort (:opts opts+args))]
    (config-set :cohort cohort)))

(defn mblog-cohort [_opts+args]
  (when-let [editor (config-get :cohort)]
    (println editor)))

(defn mblog-set-editor [opts+args]
  (when-let [editor (:editor (:opts opts+args))]
    (config-set :editor editor)))

(defn mblog-editor [_opts+args]
  (when-let [editor (config-get :editor)]
    (println editor)))

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

   {:cmds ["config" "cohort"] :fn mblog-cohort}
   {:cmds ["config" "editor"] :fn mblog-editor}
   {:cmds ["config" "repo-path"] :fn mblog-repo-path}

   {:cmds ["config" "set" "cohort"] :fn mblog-set-cohort :args->opts [:cohort]}
   {:cmds ["config" "set" "editor"] :fn mblog-set-editor :args->opts [:editor]}
   {:cmds ["config" "set" "repo-path"] :fn mblog-set-repo-path :args->opts [:repo-path]}

   {:cmds [] :fn mblog-help}])

(defn -main [& args]
  (cli/dispatch subcommands
                args
                {:coerce {:repo-path :string
                          :editor :string
                          :cohort :keyword}}))
