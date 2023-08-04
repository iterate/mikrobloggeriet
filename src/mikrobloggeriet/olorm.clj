;; # olorm core library
;;
;; shared between CLI and server. Runs on both JVM Clojure and Babashka.

(ns mikrobloggeriet.olorm
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [babashka.process :refer [shell]]
   [clojure.pprint :refer [pprint]]))

TRY TO BREAK MASTER
SEE WHAT HAPPENS

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
  (let [olorm (sorted-map)
        olorm (assoc olorm :cohort :olorm)
        olorm (if repo-path (assoc olorm :repo-path (str (fs/canonicalize repo-path))) olorm)
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

(defn validate
  "Ensures that `doc` is a valid OLORM document"
  [doc]
  (assert (:slug doc) ":slug is required!")
  (assert (:repo-path doc) ":repo-path is required!")
  (assert (:number doc) ":number is required!"))

(defn coerce [doc]
  (let [doc (->olorm doc)]
    (validate doc)
    doc))

(def ^:private olorms-folder "o")

(defn href
  "Link to an olorm"
  [olorm]
  (let [olorm (->olorm olorm)]
    (assert (:slug olorm) "Need a slug to create an HREF for an olorm!")
    (str "/" olorms-folder "/" (:slug olorm) "/")))

(defn docs
  "All olorms sorted by olorm number"
  [{:keys [repo-path]}]
  (assert repo-path)
  (->> (fs/list-dir (fs/file repo-path olorms-folder))
       (map (fn [f]
              (->olorm {:repo-path repo-path :slug (fs/file-name f)})))
       (filter :number)
       (sort-by :number)
       (map #(assoc % :repo-path repo-path))))

(defn next-number [docs]
  (inc (or (->> docs
                (map :number)
                sort
                last)
           0)))

(def olorms docs)

(defn random
  "Pick an olorm at random"
  [{:keys [repo-path]}]
  (rand-nth (docs {:repo-path repo-path})))

(comment
  (when-let [html (requiring-resolve 'nextjournal.clerk/html)]
    (docs {:repo-path ".."})))

(defn md-skeleton [olorm]
  (let [title (or (when (:number olorm) (str "OLORM-" (:number olorm)))
                  (:slug olorm)
                  "OLORM")]
    (str "# " title "\n\n"
         (str/trim "
<!-- 1. Hva gjør du akkurat nå? -->

<!-- 2. Finner du kvalitet i det? -->

<!-- 3. Hvorfor / hvorfor ikke? -->

<!-- 4. Call to action---hva ønsker du kommentarer på fra de som leser? -->
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
  (get {"git@teod.eu" "Teodor",
        "lars.barlindhaug@iterate.no" "Lars",
        "oddmunds@iterate.no" "Oddmund",
        "richard.tingstad@iterate.no" "Richard"}
       (:git.user/email doc)))

(defn save-meta! [doc+meta]
  (validate doc+meta)
  (binding [*print-namespace-maps* false]
    (spit (meta-path doc+meta)
          (with-out-str (pprint (into (sorted-map) (dissoc doc+meta :slug :number :repo-path)))))))

(defn git-user-email [{:keys [repo-path]}]
  (str/trim (:out (shell {:out :string :dir repo-path} "git config user.email"))))

(defn infer-created-date [{:keys [repo-path] :as doc}]
  (-> (shell {:dir repo-path :out :string}
             "git log --pretty=\"format:%cs\""
             (index-md-path doc))
      :out
      str/split-lines
      sort
      first))

(comment
  (fs/canonicalize "..")

  (infer-created-date (->olorm {:number 1 :repo-path "/home/teodorlu/dev/iterate/olorm"}))

  (->olorm {:number 1 :repo-path "/home/teodorlu/dev/iterate/olorm"})

  (fs/absolutize "..")
  (fs/canonicalize "..")

  (infer-created-date (->olorm {:number 1 :repo-path ".."}))
  (:out
   (shell {:out :string} "pwd"))
  "/Users/teodorlu/dev/iterate/olorm/lib\n"


  (infer-created-date (->olorm {:number 1 :repo-path "/Users/teodorlu/dev/iterate/olorm"}))
  )

(defn uuid []
  (str (java.util.UUID/randomUUID)))

(defn today []
  (.format (java.time.LocalDateTime/now)
           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(defn exists? [olorm]
  (assert (and (contains? olorm :slug) (contains? olorm :repo-path)) "Required keys: :slug and :repo-path")
  (and (fs/directory? (path olorm))
       (fs/exists? (index-md-path olorm))))
