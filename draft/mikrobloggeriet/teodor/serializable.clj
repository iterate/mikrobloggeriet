;; # serializable

(ns mikrobloggeriet.teodor.serializable
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.walk]))

(defn serializable? [x]
  (try
    (= x
       (-> x pr-str edn/read-string))
    (catch Exception e
      false)))

(serializable? {:x 1 :y 2})
(serializable? (io/file "file.txt"))

(empty (first {:x 1 :y 2}))

(defn sanitize [coll]
  (clojure.walk/postwalk
   (fn [el-or-seq]
     (cond
       (seqable? el-or-seq)
       (into (empty el-or-seq)
             (filter serializable? el-or-seq))
       :else
       el-or-seq))
   coll))

;; works fine on vectors:
(sanitize [1 2 3 (io/file "nr 4") 5 6])

;; but doesn't work on maps.
;; Not sure how to handle map entries.
(comment
  ;; this crashes:
  (sanitize {:x 1 :name "teodor" :file (io/file "secrets")})
  )
