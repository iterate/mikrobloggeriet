;; # olorm core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

(ns olorm.lib
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]))

;; olom data format example:

{:slug "olorm-1" :olorm 1 :number 1 :repo-path "."}

;; :slug is a string. :slug will appear in the URL.
;; :olorm is an int.

(defn slug->olorm [slug]
  (let [olorm-str (second (re-find #"olorm-([0-9]+)" slug))]
    (if olorm-str
      {:slug slug
       :olorm (edn/read-string olorm-str)
       :number (edn/read-string olorm-str)}
      {:slug slug})))

(defn ^:private parse-slug [slug]
  (when-let [number (second (re-find #"olorm-([0-9]+)" slug))]
    (edn/read-string number)))

(defn ->olorm
  "Try creating an olorm from \"what we've got\".

  Examples:

    (->olorm {:slug \"olorm-2\"})
    ;; => {:slug \"olorm-2\" :number 2}

    (->olorm {:number 3})
    ;; => {:slug \"olorm-3\" :number 3}
  "
  [{:keys [slug number repo-path]}]
  (let [olorm {}
        olorm (if repo-path (assoc olorm :repo-path repo-path) olorm)
        olorm (if number
                (assoc olorm
                       :number number
                       :slug (str "olorm-" number))
                olorm)
        olorm (if (and slug (not number))
                (when-let [number (parse-slug slug)]
                  (assoc olorm
                         :slug slug
                         :number number))
                olorm)]
    olorm))

(defn olorms [{:keys [repo-path]}]
  (->> (fs/list-dir (fs/file repo-path "o"))
       (map fs/file-name)
       (map slug->olorm)
       (filter :olorm)
       (map #(assoc % :repo-path repo-path))))

(defn md-skeleton [{:keys [olorm]}]
  (str "# OLORM-" olorm "\n\n"
       (str/trim "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
")))

(defn path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/path (:repo-path olorm) "o" (:slug olorm)))

(defn index-md-path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/file (path olorm) "index.md"))

(defn exists? [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (and (fs/directory? (path olorm))
       (fs/exists? (index-md-path olorm))))

;; ## TODO
;;
;; - [ ] Move olorm config in here.
;;       Why?
;;       Because we already _know_ the olorm repo path (with config), but it's locked away in the CLI.
;;       If we had the config here, we could write code that "just works when config is set".
