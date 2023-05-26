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
  (let [doc (sorted-map)
        doc (assoc doc :cohort :jals)
        doc (if repo-path (assoc doc :repo-path repo-path) doc)
        doc (if number
                (assoc doc
                       :number number
                       :slug (str "jals-" number))
                doc)
        doc (if (and slug (not number))
                (when-let [number (parse-slug slug)]
                  (assoc doc
                         :slug slug
                         :number number))
                doc)]
    doc))

(def ->doc ->jals)

(defn validate
  "Ensures that `doc` is a valid JALS document"
  [doc]
  (assert (:slug doc) ":slug is required!")
  (assert (:repo-path doc) ":repo-path is required!")
  (assert (:number doc) ":number is required!"))

(defn coerce [doc]
  (let [doc (->doc doc)]
    (validate doc)
    doc))

(def ^:private docs-folder "j")

(defn href
  "Link to a doc"
  [doc]
  (let [doc (->jals doc)]
    (assert (:slug doc) "Need a slug to create an HREF for a jals!")
    (str "/" docs-folder "/" (:slug doc) "/")))

(defn docs
  "All jals docs sorted by number"
  [{:keys [repo-path]}]
  (assert repo-path)
  (->> (fs/list-dir (fs/file repo-path docs-folder))
       (map (fn [f]
              (->jals {:repo-path repo-path :slug (fs/file-name f)})))
       (filter :number)
       (sort-by :number)
       (map #(assoc % :repo-path repo-path))))

(defn random
  "Pick a JALS at random"
  [{:keys [repo-path]}]
  (rand-nth (docs {:repo-path repo-path})))

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

(defn load-meta [doc]
  (let [doc (coerce doc)]
    (let [meta (if (fs/exists? (meta-path doc))
                 (edn/read-string (slurp (meta-path doc)))
                 {})]
      (assoc meta
             :slug (:slug doc)
             :number (:number doc)
             :repo-path (:repo-path doc)))))

(defn author-name [doc]
  (get (zipmap '("adrian.tofting@iterate.no" "sindre@iterate.no" "larshbj@gmail.com")
               ["Adrian" "Sindre" "Lars"])
       (:git.user/email doc)))

(defn git-user-email [{:keys [repo-path]}]
  (str/trim (:out (shell {:out :string :dir repo-path} "git config user.email"))))

(defn uuid []
  (str (java.util.UUID/randomUUID)))

(defn today []
  (.format (java.time.LocalDateTime/now)
           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(defn exists? [doc]
  (assert (and (contains? doc :slug) (contains? doc :repo-path)) "Required keys: :slug and :repo-path")
  (and (fs/directory? (path doc))
       (fs/exists? (index-md-path doc))))
