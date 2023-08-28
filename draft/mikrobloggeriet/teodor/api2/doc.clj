(ns mikrobloggeriet.teodor.api2.doc
  "A doc is a text with a URL"
  (:require
   [clojure.string :as str]))

(defn from-slug [slug]
  {:doc/slug slug})

(defn slug [doc]
  (:doc/slug doc))

(defn number [doc]
  (when-let [slug (:doc/slug doc)]
    (parse-long (last (str/split slug #"-")))))
