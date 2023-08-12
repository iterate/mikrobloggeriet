(ns mikrobloggeriet.version
  (:require
   [clojure.pprint :refer [pprint]]
   [babashka.process]))

(defn- today []
  (.format (java.time.LocalDateTime/now)
           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(defn info
  []
  (let [information {:date (today)}
        information (if-let [git-sha (System/getenv "HOPS_GIT_SHA")]
                      (assoc information :git/sha git-sha)
                      information)]
    information))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn prn-info [_]
  (pprint (info)))
