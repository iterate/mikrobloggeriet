(ns mikrobloggeriet.cli
  (:require
   [babashka.cli :as cli]
   [clojure.pprint :refer [pprint]]
   [babashka.fs :as fs]
   [clojure.edn :as edn]))

(defn- config-file []
  (fs/file (fs/xdg-config-home "mikrobloggeriet") "config.edn"))

(defn- load-config []
  (into (sorted-map)
        (if (fs/exists? (config-file))
          (edn/read-string (slurp (config-file)))
          {})))

(defn config-get [property]
  (get (load-config) property))

(comment
  (config-get :repo-path))

(defn config-set [property value]
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

(defn mblog-set-repo-path [opts+args])
(defn mblog-repo-path [_opts+args])
(defn mblog-set-cohort [opts+args])
(defn mblog-cohort [_opts+args])
(defn mblog-set-editor [opts+args])
(defn mblog-editor [_opts+args])

(defn mblog-help [opts]
  (prn :help))

(def subcommands
  [
   {:cmds ["set-repo-path"] :fn mblog-set-repo-path :args->opts [:path]}
   {:cmds ["repo-path"] :fn mblog-repo-path}
   {:cmds ["set-cohort"] :fn mblog-set-cohort :args->opts [:cohort]}
   {:cmds ["cohort"] :fn mblog-cohort}
   {:cmds ["set-editor"] :fn mblog-set-editor :args->opts [:editor]}
   {:cmds ["editor"] :fn mblog-editor}
   {:cmds ["help"] :fn mblog-help}
   {:cmds [] :fn mblog-help}])

(defn -main [& args]
  (cli/dispatch subcommands args))
