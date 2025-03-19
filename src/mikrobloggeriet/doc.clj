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
  (or (:doc/title doc)
      (:title (cache/parse-markdown (:doc/markdown doc)))))

(defn html [doc]
  (:doc/html (cache/parse-markdown (:doc/markdown doc))))

(defn hiccup [doc]
  (:doc/hiccup (cache/parse-markdown (:doc/markdown doc))))

(defn description [doc]
  (:description (cache/parse-markdown (:doc/markdown doc))))

(defn remove-cohort-prefix [title]
  (str/replace title #"^[A-Z]+-\d+[: -]+ " ""))

(defn cleaned-title [doc]
  (-> doc title remove-cohort-prefix))

(comment
  (cache/parse-markdown "# Funksjonell programmering")
  (title {:doc/markdown "# Funksjonell programmering"})
  (html {:doc/markdown "# Funksjonell programmering"})
  )

(defn all [db]
  (->> (d/q '[:find [?eid ...]
              :where [?eid :doc/slug]]
            db)
       (map (fn [eid]
              (d/entity db eid)))))

(defn latest [db]
  (->> (all db)
       (filter :doc/created)
       (sort-by :doc/created)
       reverse))

(defn random-published [db]
  (rand-nth (->> (all db)
                 (remove :doc/draft?))))

(comment
  (require '[mikrobloggeriet.state :as state])
  (def db state/datomic)
  (random-published db)

  )
