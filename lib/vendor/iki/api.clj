;; # Vendored IKI API
;;
;; We choose to prototype the functionality we need from IKI close to its use
;; before solidification.

(ns iki.api
  (:require
   [clojure.string :as str]
   [babashka.process]))

(defn markdown->html
  "Converts mardown to html by shelling out to Pandoc"
  [markdown]
  (when (string? markdown)
    (slurp (:out (babashka.process/process "pandoc --from markdown+smart --to html" {:in markdown})))))

(defn cache-fn-by
  "A simple in-memory caching mechanism"
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

;; ## Clerk example usage

(when-let [html (requiring-resolve 'nextjournal.clerk/html)]
  (html
   (markdown->html (str/trim "
### Welcome

This is _it_.
"))))
