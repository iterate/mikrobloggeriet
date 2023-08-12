(ns mikrobloggeriet.pandoc
  (:require
   [clojure.string :as str]
   [babashka.process]
   [cheshire.core :as json]))

(defn pandoc? [x]
  (and
   (map? x)
   (contains? x :pandoc-api-version)
   (contains? x :blocks)
   (contains? x :meta)))

(defn markdown-> [markdown]
  (when (string? markdown)
    (let [process-handle (deref (babashka.process/process {:in markdown :out :string}
                                                          "pandoc --from markdown+smart --to json"))]
      (when (= 0 (:exit process-handle))
        (json/parse-string (:out process-handle) keyword)))))

(defn- run-pandoc [stdin command]
  (let [process-handle (deref (babashka.process/process {:in stdin :out :string} command))]
    (when (= 0 (:exit process-handle))
      (:out process-handle))))

(defn ->html [pandoc]
  (when (pandoc? pandoc)
    (let [process-handle (deref (babashka.process/process {:in (json/generate-string pandoc)
                                                           :out :string}
                                                          "pandoc --from json --to html"))]
      (when (= 0 (:exit process-handle))
        (:out process-handle)))))

(defn ->html-standalone [pandoc]
  (when (pandoc? pandoc)
    (let [process-handle (deref (babashka.process/process {:in (json/generate-string pandoc)
                                                           :out :string}
                                                          "pandoc --standalone --from json --to html"))]
      (when (= 0 (:exit process-handle))
        (:out process-handle)))))

(defn html-> [pandoc]
  (when (string? pandoc)
    (let [process-handle (deref (babashka.process/process {:in (json/generate-string pandoc)
                                                           :out :string}
                                                          "pandoc --from html --to json"))]
      (when (= 0 (:exit process-handle))
        (json/parse-string (:out process-handle) keyword)))))


(defn ->markdown [pandoc]
  (when (pandoc? pandoc)
    (let [process-handle (deref (babashka.process/process {:in (json/generate-string pandoc)
                                                           :out :string}
                                                          "pandoc --from json --to markdown"))]
      (when (= 0 (:exit process-handle))
        (:out process-handle)))))

(defn ->markdown-standalone [pandoc]
  (when (pandoc? pandoc)
    (let [process-handle (deref (babashka.process/process {:in (json/generate-string pandoc)
                                                           :out :string}
                                                          "pandoc --standalone --from json --to markdown"))]
      (when (= 0 (:exit process-handle))
        (:out process-handle)))))

(defn ->org [pandoc]
  (when (pandoc? pandoc)
    (let [process-handle (deref (babashka.process/process {:in (json/generate-string pandoc)
                                                           :out :string}
                                                          "pandoc --from json --to org"))]
      (when (= 0 (:exit process-handle))
        (:out process-handle))))
  )

(defn el->plaintext
  "Convert a pandoc expression to plaintext without shelling out to pandoc"
  [expr]
  (cond (= "Str" (:t expr))
        (:c expr)
        (= "MetaInlines" (:t expr))
        (str/join (->> (:c expr)
                       (map el->plaintext)
                       (filter some?)))
        (= "Space" (:t expr))
        " "
        (= "Para" (:t expr))
        (str/join (->> (:c expr)
                       (map el->plaintext)
                       (filter some?)))
        (= "Emph" (:t expr))
        (str/join (->> (:c expr)
                       (map el->plaintext)
                       (filter some?)))
        :else nil))

(defn set-title [pandoc title]
  (assert (pandoc? pandoc))
  (assoc-in pandoc [:meta :title] {:t "MetaInlines" :c [{:t "Str" :c title}]}))

(let [doc "% viktig melding

hei heiii

Er dere **klare**??"]
  (-> doc
      (markdown->)
      (->markdown-standalone)))

(spit "example.html"
      (let [doc "hei duu"]
        (-> doc
            (markdown->)
            (set-title "viktig melding")
            (->html-standalone))))

(defn title [pandoc]
  (when-let [title-el (-> pandoc :meta :title)]
    (el->plaintext title-el)))

(defn markdown->html
  "Converts mardown to html by shelling out to Pandoc"
  [markdown]
  (-> markdown markdown-> ->html))

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
