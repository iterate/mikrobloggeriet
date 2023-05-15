;; # jals core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

(ns mikrobloggeriet.jals
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [babashka.process :refer [shell]]))

;; jals example:

{:slug "jals-1" :number 1 :repo-path "."}

(defn ^:private parse-slug [slug]
  (when-let [number (second (re-find #"jals-([0-9]+)" slug))]
    (edn/read-string number)))

(comment
  (when-let [html (requiring-resolve 'nextjournal.clerk/html)]
    (parse-slug "jals-42")))

(defn ->jals
  "Try creating a jals doc from \"what we've got\".

  Examples:

    (->jals {:slug \"jals-2\"})
    ;; => {:slug \"jals-2\" :number 2}

    (->jals {:number 3})
    ;; => {:slug \"jals-3\" :number 3}
  "
  [{:keys [slug number repo-path]}]
  (let [doc {}
        doc (if repo-path (assoc doc :repo-path repo-path) doc)
        doc (if number
                (assoc doc
                       :number number
                       :slug (str "olorm-" number))
                doc)
        doc (if (and slug (not number))
                (when-let [number (parse-slug slug)]
                  (assoc doc
                         :slug slug
                         :number number))
                doc)]
    doc))

(def ^:private docs-folder "j")

(defn href
  "Link to a doc"
  [doc]
  (let [doc (->jals doc)]
    (assert (:slug doc) "Need a slug to create an HREF for an olorm!")
    (str "/" docs-folder "/" (:slug doc) "/")))

(defn docs
  "All olorms sorted by olorm number"
  [{:keys [repo-path]}]
  (assert repo-path)
  (->> (fs/list-dir (fs/file repo-path docs-folder))
       (map (fn [f]
              (->jals {:repo-path repo-path :slug (fs/file-name f)})))
       (filter :number)
       (sort-by :number)
       (map #(assoc % :repo-path repo-path))))

(comment
  (when-let [html (requiring-resolve 'nextjournal.clerk/html)]
    (docs {:repo-path ".."})))

(defn md-skeleton [doc]
  (let [title (or (when (:number doc) (str "JALS-" (:number doc)))
                  (:slug doc)
                  "JALS")]
    (str "# " title "\n\n"
         (str/trim "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->
"))))

(defn path [doc]
  (assert (and (contains? doc :slug) (contains? doc :repo-path)) "Required keys: :slug and :repo-path")
  (fs/path (:repo-path doc) docs-folder (:slug doc)))

(defn index-md-path [doc]
  (assert (and (contains? doc :slug) (contains? doc :repo-path)) "Required keys: :slug and :repo-path")
  (fs/file (path doc) "index.md"))

(defn meta-path [doc]
  (assert (and (contains? doc :slug) (contains? doc :repo-path)) "Required keys: :slug and :repo-path")
  (fs/file (path doc) "meta.edn"))

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

;; ## TODO
;;
;; - [ ] Move olorm config in here.
;;       Why?
;;       Because we already _know_ the olorm repo path (with config), but it's locked away in the CLI.
;;       If we had the config here, we could write code that "just works when config is set".
