(ns mikrobloggeriet.cache)

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
                 (if-let [fn-name (:name (meta f))]
                   (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key wrapping" fn-name)
                   (warn-fn "Warning:" 'fn-with-cache "recived a non-string cache key"))))
             result)))))))

(comment
  (defn slow+ [a b]
    (Thread/sleep 200)
    (+ a b))

  (slow+ 10 20)
  ;; slow

  (def fast+ (cache-fn-by slow+ #(str %1 " " %2)))
  ;; fast after values have been cached.

  (fast+ 11 2)
  (fast+ 1 12)
  (fast+ 9 999)
  (fast+ 99 99))
