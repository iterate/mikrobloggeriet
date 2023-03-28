(ns olorm.lib
  (:require [babashka.fs :as fs]))

;; olorm core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

(defn pages [{:keys [repo-path]}]
  (for [folder (fs/list-dir (str repo-path "/p"))]
    {:slug (fs/file-name folder)}))
