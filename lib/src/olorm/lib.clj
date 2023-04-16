(ns olorm.lib
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]))

;; olorm core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

;; olom data format example:

{:slug "olorm-1" :olorm 1 :repo-path "."}

;; :slug is a string. :slug will appear in the URL.
;; :olorm is an int.

(defn slug->olorm [slug]
  (let [olorm-str (second (re-find #"olorm-([0-9]+)" slug))]
    (if olorm-str
      {:slug slug :olorm (edn/read-string olorm-str)}
      {:slug slug})))

(defn olorms [{:keys [repo-path]}]
  (->> (fs/list-dir (fs/file repo-path "p"))
       (map fs/file-name)
       (map slug->olorm)
       (filter :olorm)
       (map #(assoc % :repo-path repo-path))))

(defn path [{:keys [olorm repo-path]}]
  (when (and olorm repo-path)
    (str repo-path "/p/olorm-" olorm)))

(defn md-skeleton [{:keys [olorm]}]
  (str "# OLORM-" olorm "\n\n"
       (str/trim "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
")))

(defn path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/path (:repo-path olorm) "p" (:slug olorm)))

(defn index-md-path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/file (path olorm) "index.md"))

(defn exists? [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (and (fs/directory? (path olorm))
       (fs/exists? (index-md-path olorm))))
