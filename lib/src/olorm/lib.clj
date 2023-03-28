(ns olorm.lib
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]))

;; olorm core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

;; olom data format

{:slug "olorm-1" :olom 1}

(defn slug->olorm [slug]
  (let [olorm-str (second (re-find #"olorm-([0-9]+)" slug))]
    (if olorm-str
      {:slug slug :olorm (edn/read-string olorm-str)}
      {:slug slug})))

(defn olorms [{:keys [repo-path]}]
  (->> (fs/list-dir (str repo-path "/p"))
       (map str)
       (map slug->olorm)
       (filter :olorm)))
