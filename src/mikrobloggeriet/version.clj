(ns mikrobloggeriet.version
  (:require [clojure.pprint :refer [pprint]]))

(defn- today []
  (.format (java.time.LocalDateTime/now)
           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(defn info
  []
  {:date (today)})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn prn-info [_]
  (pprint (info)))
