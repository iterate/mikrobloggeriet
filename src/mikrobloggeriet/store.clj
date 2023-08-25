(ns mikrobloggeriet.store
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.doc :as doc]))

(def olorm
  (sorted-map
   :cohort/root "o"
   :cohort/id :olorm
   :cohort/members [{:author/email "git@teod.eu", :author/first-name "Teodor"}
                    {:author/email "lars.barlindhaug@iterate.no", :author/first-name "Lars"}
                    {:author/email "oddmunds@iterate.no", :author/first-name "Oddmund"}
                    {:author/email "richard.tingstad@iterate.no", :author/first-name "Richard"}]))

(def jals
  (sorted-map
   :cohort/root "j"
   :cohort/id :jals
   :cohort/members [{:author/email "aaberg89@gmail.com", :author/first-name "Jørgen"}
                    {:author/email "adrian.tofting@iterate.no",
                     :author/first-name "Adrian"}
                    {:author/email "larshbj@gmail.com", :author/first-name "Lars"}
                    {:author/email "sindre@iterate.no", :author/first-name "Sindre"}]))

(def oj
  (sorted-map
   :cohort/root "text/oj"
   :cohort/id :oj
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}]))

(def genai
  (sorted-map
   :cohort/root "text/genai"
   :cohort/id :genai
   :cohort/members [{:author/first-name "Julian"}]))

(def cohorts
  (->> [olorm jals oj genai]
       (map (fn [c]
              [(:cohort/id c) c]))
       (into (sorted-map))))

(defn cohort-docs [cohort]
  (let [id (:cohort/id cohort)
        root (:cohort/root cohort)]
    (when (and id root (fs/directory? root))
      (->> (fs/list-dir (fs/file root))
           (map (fn [f]
                  {:doc/slug (fs/file-name f)}))
           (filter (partial doc/exists? cohort))
           (sort-by doc/number)))))


(defn doc-exists? [cohort doc]
  (and (:cohort/root cohort)
       (:doc/slug doc)
       (fs/directory? (fs/file (:cohort/root cohort)
                               (:doc/slug doc)))
       (fs/exists? (fs/file (:cohort/root cohort)
                            (:doc/slug doc)
                            "meta.edn"))
       (fs/exists? (fs/file (:cohort/root cohort)
                            (:doc/slug doc)
                            "index.md"))))

(defn doc-href
  "Create a link to a doc in a cohort"
  [cohort doc]
  (when (and (:cohort/id cohort)
             (:doc/slug doc))
    (str "/" (name (:cohort/id cohort)) "/" (:doc/slug doc))))

(defn doc-index-md-path [cohort doc]
  (when (doc-exists? cohort doc)
    (fs/file (:cohort/root cohort)
             (:doc/slug doc)
             "index.md")))
