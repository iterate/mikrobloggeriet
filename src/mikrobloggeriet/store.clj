(ns mikrobloggeriet.store
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.cohort :as cohort]
   [mikrobloggeriet.doc :as doc]
   [clojure.edn :as edn]))

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
   :cohort/members [{:author/email "jomarn@me.com" :author/first-name "Johan"}
                    {:author/email "olav.moseng@iterate.no" :author/first-name "Olav"}]))

(def genai
  (sorted-map
   :cohort/root "text/genai"
   :cohort/slug "genai"
   :cohort/members [{:author/first-name "Julian"}]))

(def cohorts (sorted-map :olorm olorm :jals jals :oj oj :genai genai))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HELPERS

(defn doc-folder [cohort doc]
  (fs/file (cohort/repo-path cohort)
           (cohort/root cohort)
           (doc/slug doc)))


(defn doc-md-path [cohort doc]
  (fs/file (doc-folder cohort doc)
           "index.md"))

(defn doc-meta-path [cohort doc]
  (fs/file (doc-folder cohort doc)
           "meta.edn"))

(defn doc-exists? [cohort doc]
  (and (cohort/root cohort)
       (doc/slug doc)
       (fs/exists? (doc-md-path cohort doc))
       (fs/exists? (doc-meta-path cohort doc))))

(defn load-meta [cohort doc]
  (let [meta (edn/read-string (slurp (doc-meta-path cohort doc)))]
    (merge doc meta)))

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

(defn author-first-name [cohort doc]
  (let [email->first-name (->> (cohort/members cohort)
                               (map (juxt :author/email :author/first-name))
                               (into {}))]
    (email->first-name (:git.user/email (load-meta cohort doc)))))

(comment
  (author-first-name oj (first (docs oj)))

  (->> (cohort/members oj)
       (map (juxt :author/email :author/first-name))
       (into {}))
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LIST DOCUMENTS

(defn docs [cohort]
  (let [root (fs/file (cohort/repo-path cohort)
                      (cohort/root cohort))]
    (when (fs/directory? root)
      (->> (fs/list-dir (fs/file root))
           (map (comp doc/from-slug fs/file-name))
           (filter (partial doc-exists? cohort))
           (sort-by doc/number)))))

(defn next-doc
  "Creates a new doc for a cohort"
  [cohort]
  (let [number (inc (or (->> (docs cohort)
                             last
                             doc/number)
                        0))]
    (doc/from-slug (str (cohort/slug cohort) "-" number))))

(defn random-cohort+doc
  "Returns a random tuple of (cohort, doc) from all known cohorts"
  []
  (->> [olorm jals oj]
       (mapcat (fn [cohort]
                 (for [d (docs cohort)]
                   [cohort d])))
       (into [])
       rand-nth))
