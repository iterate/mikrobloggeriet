;; # olorm core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

(ns olorm.lib
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [babashka.process :refer [shell]]))

;; olorm example:

{:slug "olorm-1" :number 1 :repo-path "."}

(defn ^:private parse-slug [slug]
  (when-let [number (second (re-find #"olorm-([0-9]+)" slug))]
    (edn/read-string number)))

(comment
  (when-let [html (requiring-resolve 'nextjournal.clerk/html)]
    (parse-slug "olorm-42")))

(defn ->olorm
  "Try creating an olorm from \"what we've got\".

  Examples:

    (->olorm {:slug \"olorm-2\"})
    ;; => {:slug \"olorm-2\" :number 2}

    (->olorm {:number 3})
    ;; => {:slug \"olorm-3\" :number 3}
  "
  [{:keys [slug number repo-path]}]
  (binding [*out* *err*]
    (println "WARNING: running on legacy OLORM backend. Please reinstall your CLI, see README.md."))
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

(def ^:private olorms-folder "o")

(defn href
  "Link to an olorm"
  [olorm]
  (let [olorm (->olorm olorm)]
    (assert (:slug olorm) "Need a slug to create an HREF for an olorm!")
    (str "/" olorms-folder "/" (:slug olorm) "/")))

(defn olorms
  "All olorms sorted by olorm number"
  [{:keys [repo-path]}]
  (assert repo-path)
  (->> (fs/list-dir (fs/file repo-path olorms-folder))
       (map (fn [f]
              (->olorm {:repo-path repo-path :slug (fs/file-name f)})))
       (filter :number)
       (sort-by :number)
       (map #(assoc % :repo-path repo-path))))

(comment
  (when-let [html (requiring-resolve 'nextjournal.clerk/html)]
    (olorms {:repo-path ".."})))

(defn md-skeleton [olorm]
  (let [title (or (when (:number olorm) (str "OLORM-" (:number olorm)))
                  (:slug olorm)
                  "OLORM")]
    (str "# " title "\n\n"
         (str/trim "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
"))))

(defn path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/path (:repo-path olorm) olorms-folder (:slug olorm)))

(defn index-md-path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/file (path olorm) "index.md"))

(defn meta-path [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (fs/file (path olorm) "meta.edn"))

(defn git-user-email [{:keys [repo-path]}]
  (str/trim (:out (shell {:out :string :dir repo-path} "git config user.email"))))

(defn uuid []
  (str (java.util.UUID/randomUUID)))

(defn today []
  (.format (java.time.LocalDateTime/now)
           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(defn exists? [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (and (fs/directory? (path olorm))
       (fs/exists? (index-md-path olorm))))
