(ns mikrobloggeriet.pandoc
  (:require
   [clojure.string :as str]
   [babashka.process]
   [cheshire.core :as json]))

(defn from-json-str [s]
  (json/parse-string s keyword))

(defn to-json-str [x]
  (json/generate-string x))

(defn pandoc? [x]
  (and
   (map? x)
   (contains? x :pandoc-api-version)
   (contains? x :blocks)
   (contains? x :meta)))

(defn- run-pandoc [stdin command]
  (let [process-handle (deref (babashka.process/process {:in stdin :out :string} command))]
    (when (= 0 (:exit process-handle))
      (:out process-handle))))

;; READ FROM FORMAT INTO IR

(defn from-markdown [markdown-str]
  (when (string? markdown-str)
    (from-json-str (run-pandoc markdown-str "pandoc --from markdown+smart --to json"))))

(defn from-html [html-str]
  (when (string? html-str)
    (from-json-str (run-pandoc html-str "pandoc --from html --to json"))))

(defn from-org [org-str]
  (when (string? org-str)
    (from-json-str (run-pandoc org-str "pandoc --from org+smart --to json"))))

;; WRITE IR TO FORMAT

(defn to-html [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) "pandoc --from json --to html")))

(defn to-html-standalone [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) "pandoc --standalone --from json --to html")))

(defn to-markdown [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) "pandoc --from json --to markdown")))

(defn to-markdown-standalone [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) "pandoc --standalone --from json --to markdown")))

(defn to-org [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) "pandoc --from json --to org")))

(defn to-org-standalone [pandoc]
  (when (pandoc? pandoc)
    (run-pandoc (to-json-str pandoc) "pandoc --standalone --from json --to org")))

;; HELPERS

(declare el->plaintext)

(defn els->plaintext [els]
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
        (= "Para" (:t expr))
        (els->plaintext (:c expr))
        (= "Emph" (:t expr))
        (els->plaintext (:c expr))
        :else nil))

(defn set-title [pandoc title]
  (assert (pandoc? pandoc))
  (assoc-in pandoc [:meta :title] {:t "MetaInlines" :c [{:t "Str" :c title}]}))

(defn title [pandoc]
  (when-let [title-el (-> pandoc :meta :title)]
    (el->plaintext title-el)))

(defn header? [el ]
  (= (:t el) "Header"))

(defn h1? [el]
  (let [header-level (-> el :c first)]
    (and (header? el)
         (= header-level 1))))

(defn header->plaintext [el]
  (when (header? el)
    (els->plaintext (get-in el [:c 2]))))

(defn infer-title [pandoc]
  (or (title pandoc)
      (when-let [first-h1 (->> (:blocks pandoc)
                               (filter h1?)
                               first)]
        (header->plaintext first-h1))))

(defn markdown->html
  "Converts mardown to html by shelling out to Pandoc"
  [markdown]
  (-> markdown from-markdown to-html))

(defn markdown->html+info [markdown]
  (let [pandoc (from-markdown markdown)]
    {:html (to-html pandoc)
     :title (title pandoc)}))

(defn cache-fn-by
  "A simple in-memory caching mechanism

  Example usage:

    (def cached+ (cache-fn-by slow+ (fn [a b] (str a \" \" b)))

    (cached+ 1 2) ; normal speed first time
    (cached+ 1 2) ; next invokation is a cache lookup

  - `f` is the function you want to cache

  - `cache-key-fn` is a function taking the same arguments as `f`, returning a
    string which is used as cache key.

  - `warn-fn` (optional, function from string to any) is called when the caching
    mechanism fails. It defaults to nil, no reporting."
  ([f cache-key-fn] (cache-fn-by f cache-key-fn {}))
  ([f cache-key-fn {:keys [warn-fn]}]
   (let [cache (atom {})]
     (fn [& args]
       (let [cache-key (apply cache-key-fn args)]
         (if (contains? @cache cache-key)
           (get @cache cache-key)
           (let [result (apply f args)]
             (if (string? cache-key)
               (swap! cache assoc cache-key result)
               ;; Otherwise, warn that the cache key isn't valid
               (when warn-fn
                 (if-let [n (:name (meta f))]
                   (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key wrapping" n)
                   (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key"))))
             result)))))))
