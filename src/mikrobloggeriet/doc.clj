(ns mikrobloggeriet.doc
  (:refer-clojure :exclude [next])
  (:require
   [clojure.string :as str]
   [datomic.api :as d]
   [mikrobloggeriet.cache :as cache]))

(defn number [doc]
  (when-let [slug (:doc/slug doc)]
    (parse-long (last (str/split slug #"-")))))

(defn href
  [doc]
  (when-let [cohort-slug (-> doc :doc/cohort :cohort/slug)]
    (when-let [doc-slug (:doc/slug doc)]
      (str "/" cohort-slug "/" doc-slug "/"))))

(defn previous [db doc]
  (let [previous-number (dec (number doc))
        cohort (:doc/cohort doc)
        previous-slug (str (:cohort/slug cohort) "-" previous-number)]
    (d/entity db [:doc/slug previous-slug])))

(defn next [db doc]
  (let [next-number (inc (number doc))
        cohort (:doc/cohort doc)
        next-slug (str (:cohort/slug cohort) "-" next-number)]
    (d/entity db [:doc/slug next-slug])))

(defn author-first-name [db doc]
  (:author/first-name (d/entity db [:author/email (:git.user/email doc)])))

(defn title [doc]
  (:title (cache/markdown->html+info (:doc/markdown doc))))

(defn html [doc]
  (:doc-html (cache/markdown->html+info (:doc/markdown doc))))

(comment
  (cache/markdown->html+info "# Funksjonell programmering")
  (title {:doc/markdown "# Funksjonell programmering"})
  (html {:doc/markdown "# Funksjonell programmering"})
  )

(defn random-doc [db]
  (d/entity db [:doc/slug (->> (d/q '[:find [?slug ...]
                                      :where [_ :doc/slug ?slug]]
                                    db)
                               vec
                               rand-nth)]))

(comment
  (require '[mikrobloggeriet.state :as state])
  (def db state/datomic)
  (random-doc db)

  )
