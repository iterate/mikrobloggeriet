(ns mikrobloggeriet.store
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; KNOWN COHORTS

(def olorm
  (sorted-map
   :cohort/root "o"
   :cohort/slug "olorm"
   :cohort/members [{:author/email "git@teod.eu", :author/first-name "Teodor"}
                    {:author/email "lars.barlindhaug@iterate.no", :author/first-name "Lars"}
                    {:author/email "oddmunds@iterate.no", :author/first-name "Oddmund"}
                    {:author/email "richard.tingstad@iterate.no", :author/first-name "Richard"}]))

(def jals
  (sorted-map
   :cohort/root "j"
   :cohort/slug "jals"
   :cohort/members [{:author/email "aaberg89@gmail.com", :author/first-name "Jørgen"}
                    {:author/email "adrian.tofting@iterate.no",
                     :author/first-name "Adrian"}
                    {:author/email "larshbj@gmail.com", :author/first-name "Lars"}
                    {:author/email "sindre@iterate.no", :author/first-name "Sindre"}]))

(def oj
  (sorted-map
   :cohort/root "text/oj"
   :cohort/slug "oj"
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}]))

(def genai
  (sorted-map
   :cohort/root "text/genai"
   :cohort/slug "genai"
   :cohort/members [{:author/first-name "Julian"}]))

(def cohorts 
  ^:depricated
  [olorm jals oj genai])

(def cohorts-new (sorted-map :olorm olorm :jals jals :oj oj :genai genai))

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

(defn doc-folder [cohort doc]
  (fs/file (cohort/root cohort)
           (doc/slug doc))
  )

(defn doc-md-path [cohort doc]
  (fs/file (doc-folder cohort doc) 
           "index.md"))

(defn doc-meta-path [cohort doc]
  (fs/file (doc-folder cohort doc)
           "meta.edn"))

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
