(ns mikrobloggeriet.cache
  "Simple atom-backed caching for Clojure functions

  Bring your own atom, use duration if you want a durable cache."
  (:require
   [duratom.core :refer [duratom]]
   [clojure.string :as str]
   [mikrobloggeriet.pandoc :as pandoc]
   [hickory.core :as hickory]))


(defn cache-fn-by
  "A simple in-memory caching mechanism

  Example usage:

    (def cached+ (cache-fn-by slow+ (atom {}) (fn [a b] (str a \" \" b)))

    (cached+ 1 2) ; normal speed first time
    (cached+ 1 2) ; next invokation is a cache lookup

  - `cache-atom` is the atom to use as cache.

  - `f` is the function you want to cache

  - `cache-key-fn` is a function taking the same arguments as `f`, returning a
    string which is used as cache key.

  - `warn-fn` (optional, function from string to any) is called when the caching
    mechanism fails. It defaults to nil, no reporting."
  ([cache-atom f cache-key-fn]
   (cache-fn-by cache-atom f cache-key-fn {}))
  ([cache-atom f cache-key-fn {:keys [warn-fn]}]
   (fn [& args]
     (let [cache-key (apply cache-key-fn args)]
       (if (contains? @cache-atom cache-key)
         (get @cache-atom cache-key)
         (let [result (apply f args)]
           (if (string? cache-key)
             (swap! cache-atom assoc cache-key result)
             ;; Otherwise, warn that the cache key isn't valid
             (when warn-fn
               (if-let [fn-name (:name (meta f))]
                 (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key wrapping" fn-name)
                 (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key"))))
           result))))))

(comment
  (defn slow+ [a b]
    (Thread/sleep 200)
    (+ a b))

  (slow+ 10 20)
  ;; slow

  (def fast+ (cache-fn-by (atom {}) slow+ #(str %1 " " %2)))
  ;; fast after values have been cached.

  (fast+ 11 2)
  (fast+ 1 12)
  (fast+ 9 999)
  (fast+ 99 99))

(def cache-atom
  ^{:doc "Disk-backed cache atom when disk (GARDEN_STORAGE) is available"}
  (when-let [storage-path (System/getenv "GARDEN_STORAGE")]
    (duratom :local-file
             :file-path (str storage-path "/mikrobloggeriet.htmlcache.edn")
             :commit-mode :sync
             :init {})))

(defn parse-markdown* [markdown-str]
  (let [pandoc (pandoc/from-markdown markdown-str)
        html (str/trim (pandoc/to-html pandoc))]
    {:doc/html html
     :doc/hiccup (->> (hickory/as-hiccup (hickory/parse html))
                      first last
                      (drop 2))
     :title (pandoc/infer-title pandoc)
     :description (pandoc/infer-description pandoc)}))

(def parse-markdown
  (cache-fn-by (or cache-atom (atom {}))
               #'parse-markdown*
               #(str "2025-03-17-hiccup3"
                     "\n" %)
               identity))

(comment
  (parse-markdown "# Funksjonell programmering")

  (->> @cache-atom
       vals
       (map :description)
       (filter identity))

  )
