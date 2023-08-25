(ns mikrobloggeriet.teodor.api2.store
  "Mikrobloggeriet data store

  List and read known cohorts and known documents."
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.teodor.api2.cohort :as cohort]
   [mikrobloggeriet.teodor.api2.doc :as doc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; KNOWN COHORTS

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HELPERS

(defn doc-exists? [cohort doc]
  (and (cohort/root cohort)
       (doc/slug doc)
       (fs/directory? (fs/file (cohort/root cohort)
                               (doc/slug doc)))
       (fs/exists? (fs/file (cohort/root cohort)
                            (doc/slug doc)
                            "meta.edn"))
       (fs/exists? (fs/file (cohort/root cohort)
                            (:doc/slug doc)
                            "index.md"))))

(defn doc-md-path [cohort doc]
  (when (doc-exists? cohort doc)
    (fs/file (cohort/root cohort)
             (doc/slug doc)
             "index.md")))

(defn cohort-href [cohort]
  (when (cohort/slug cohort)
    (str "/" (cohort/slug cohort)
         "/")))

(defn doc-href [cohort doc]
  (when (and (cohort/slug cohort)
             (doc/slug doc))
    (str "/" (cohort/slug cohort)
         "/" (doc/slug doc)
         "/")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LIST DOCUMENTS

(defn docs [cohort]
  (let [root (cohort/root cohort)]
    (when (and root (fs/directory? root))
      (->> (fs/list-dir (fs/file root))
           (map (comp doc/from-slug fs/file-name))
           (filter (partial doc-exists? cohort))
           (sort-by doc/number)))))
