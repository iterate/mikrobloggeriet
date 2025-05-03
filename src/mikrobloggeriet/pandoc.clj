(ns mikrobloggeriet.pandoc
  (:require
   [babashka.fs :as fs]
   [babashka.process]
   [cheshire.core :as json]
   [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LOW LEVEL PANDOC WRAPPER

(defn- from-json-str [s]
  (json/parse-string s keyword))

(defn- to-json-str [x]
  (json/generate-string x))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PANDOC IR HELPERS

(defn pandoc?
  "Whether `x` looks like a pandoc document represented as Clojure data"
  [x]
  (and
   (map? x)
   (contains? x :pandoc-api-version)
   (contains? x :blocks)
   (contains? x :meta)))

(declare el->plaintext)

(defn- els->plaintext
  "Convert a sequence of pandoc expressions to plaintext without shelling out to pandoc

  els->plaintext is an implementation detail. Please use `el->plaintext` instead."
  [els]
  (str/join
   (->> els
        (map el->plaintext)
        (filter some?))))

(defn el->plaintext
  "Convert a pandoc expression to plaintext without shelling out to pandoc"
  [expr]
  (cond (= "Str" (:t expr))
        (:c expr)

        (= "MetaInlines" (:t expr))
        (els->plaintext (:c expr))

        (= "Space" (:t expr))
        " "

        (= "SoftBreak" (:t expr))
        " "

        (= "Para" (:t expr))
        (els->plaintext (:c expr))

        (= "Emph" (:t expr))
        (els->plaintext (:c expr))

        (= "Code" (:t expr))
        (second (:c expr))

        :else
        nil))

(defn set-title [pandoc title]
  (assert (pandoc? pandoc))
  (assoc-in pandoc [:meta :title] {:t "MetaInlines" :c [{:t "Str" :c title}]}))

(defn title [pandoc]
  (when-let [title-el (-> pandoc :meta :title)]
    (el->plaintext title-el)))

(defn header? [el]
  (= (:t el) "Header"))

(defn h1? [el]
  (let [header-level (-> el :c first)]
    (and (header? el)
         (= header-level 1))))

(defn para? [el]
  (= (:t el) "Para"))

(defn header->plaintext [el]
  (when (header? el)
    (els->plaintext (get-in el [:c 2]))))

(defn infer-title [pandoc]
  (or (title pandoc)
      (when-let [first-h1 (->> (:blocks pandoc)
                               (filter h1?)
                               first)]
        (header->plaintext first-h1))))

(defn infer-description [pandoc]
  (when-let [first-para (->> (:blocks pandoc)
                             (filter para?)
                             first)]
    (els->plaintext (:c first-para))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; READ FROM FORMAT INTO IR

(def pandoc-path
  "Look for a pandoc binary.

  1. First look on the user's PATH
  2. Then look inside GARDEN_STORAGE

  The GARDEN_STORAGE case is needed for Application.garden, where a Pandoc binary has been uploaded."
  (or (some-> (fs/which "pandoc") str)
      (when-let [pandoc-storage-path (some-> (System/getenv "GARDEN_STORAGE") (fs/file "pandoc") str)]
        (when (fs/exists? pandoc-storage-path)
          pandoc-storage-path))))

(defn get-pandoc-path
  "Look for a pandoc binary.

    1. First look on the user's PATH
    2. Then look inside GARDEN_STORAGE

    The GARDEN_STORAGE case is needed for Application.garden, where a Pandoc binary has been uploaded."
  []
  (or (some-> (fs/which "pandoc") str)
      (when-let [pandoc-storage-path (some-> (System/getenv "GARDEN_STORAGE") (fs/file "pandoc") str)]
        (when (fs/exists? pandoc-storage-path)
          pandoc-storage-path))))

#_ pandoc-path

(defn- run-pandoc [stdin pandoc-args]
  (let [process-handle (deref (apply babashka.process/process
                                     {:in stdin :out :string}
                                     (get-pandoc-path)
                                     pandoc-args))]
    (when (= 0 (:exit process-handle))
      (:out process-handle))))

(defn read-to-json [raw-str from-format]
  (when (string? raw-str)
    (from-json-str (run-pandoc raw-str ["--from" from-format "--to" "json"]))))

(defn from-markdown [markdown-str] (read-to-json markdown-str "markdown+smart"))
(defn from-rst [rst-str] (read-to-json rst-str "rst+smart"))
(defn from-html [html-str] (read-to-json html-str "html"))
(defn from-org [org-str] (read-to-json org-str "org+smart"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; WRITE IR TO FORMAT

(defn to-html [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--from" "json" "--to" "html"])))

(defn to-html-standalone [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--standalone" "--from" "json" "--to" "html"])))

(defn to-markdown [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--from" "json" "--to" "markdown"])))

(defn to-markdown-standalone [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--standalone" "--from" "json" "--to" "markdown"])))

(defn to-org [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--from" "json" "--to" "org"])))

(defn to-org-standalone [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--standalone" "--from" "json" "--to" "org"])))

(defn to-plain [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) ["--standalone" "--from" "json" "--to" "plain"])))
