(ns mikrobloggeriet.store
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [mikrobloggeriet.cohort.markdown :as cohort]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.doc-meta :as doc-meta]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; KNOWN COHORTS

(def olorm
  (sorted-map
   :cohort/root "o"
   :cohort/slug "olorm"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "git@teod.eu", :author/first-name "Teodor"}
                    {:author/email "lars.barlindhaug@iterate.no", :author/first-name "Lars"}
                    {:author/email "oddmunds@iterate.no", :author/first-name "Oddmund"}
                    {:author/email "richard.tingstad@iterate.no", :author/first-name "Richard"}]))

(def jals
  (sorted-map
   :cohort/root "j"
   :cohort/slug "jals"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "aaberg89@gmail.com", :author/first-name "Jørgen"}
                    {:author/email "adrian.tofting@iterate.no", :author/first-name "Adrian"}
                    {:author/email "larshbj@gmail.com", :author/first-name "Lars"}
                    {:author/email "sindre@iterate.no", :author/first-name "Sindre"}]))

(def oj
  (sorted-map
   :cohort/root "text/oj"
   :cohort/slug "oj"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "jomarn@me.com" :author/first-name "Johan"}
                    {:author/email "olav.moseng@iterate.no" :author/first-name "Olav"}]))

(def luke
  (sorted-map
   :cohort/root "text/luke"
   :cohort/slug "luke"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "haavard@vaage.com" :author/first-name "Håvard"}
                    {:author/email "julian.hallen.eriksen@iterate.no" :author/first-name "Julian"}
                    {:author/email "finn@iterate.no" :author/first-name "Finn"}
                    {:author/email "sindre@iterate.no" :author/first-name "Sindre"}
                    {:author/email "thusan@iterate.no" :author/first-name "Thusan"}
                    {:author/email "kjersti@iterate.no" :author/first-name "Kjersti"}
                    {:author/email "rasmus.stride@iterate.no" :author/first-name "Rasmus"}
                    {:author/email "pernille@iterate.no" :author/first-name "Pernille"}
                    {:author/email "richard.tingstad@iterate.no" :author/first-name "Richard"}
                    {:author/email "camilla@iterate.no" :author/first-name "Camilla"}
                    {:author/email "brunstad@iterate.no" :author/first-name "Ole Jacob"}
                    {:author/email "lars.barlindhaug@iterate.no" :author/first-name "Lars"}
                    {:author/email "ella.swan@iterate.no" :author/first-name "Ella"}
                    {:author/email "rune@iterate.no" :author/first-name "Rune"}
                    {:author/email "haugeto@iterate.no" :author/first-name "Anders"}]))

(def vakt
  (sorted-map
   :cohort/root "text/vakt"
   :cohort/slug "vakt"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "git@teod.eu", :author/first-name "Teodor"}
                    {:author/email "olav.moseng@iterate.no" :author/first-name "Olav"}
                    {:author/email "neno.mindjek@iterate.no", :author/first-name "Neno"}
                    {:author/email "julian.hallen.eriksen@iterate.no" :author/first-name "Julian"}]))

(def kiel
  (sorted-map
   :cohort/root "text/kiel"
   :cohort/slug "kiel"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "julian.hallen.eriksen@iterate.no" :author/first-name "Julian"}]))

;; for nå kaller jeg den cohort-iterate for å unngå kollisjon med clojure.core/iterate
(def cohort-iterate
  (sorted-map
   :cohort/root "text/iterate"
   :cohort/slug "iterate"
   :cohort/type :cohort.type/markdown
   :cohort/members [{:author/email "kjersti@iterate.no", :author/first-name "Kjersti"}]))

(def urlog
  (sorted-map
   :cohort/root "text/urlog"
   :cohort/slug "urlog"
   :cohort/type :cohort.type/urlog
   :cohort/members [{:author/email "neno.mindjek@iterate.no", :author/first-name "Neno"}]))


(def cohorts
  (sorted-map
   :olorm olorm
   :jals jals
   :oj oj
   :luke luke
   :urlog urlog
   :vakt vakt
   :kiel kiel
   :iterate cohort-iterate))

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

(defn doc-exists?
  "True iff doc meta and doc markdown file exists."
  [cohort doc]
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
       (into {})))

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

(defn published-docs [cohort]
  (->> (docs cohort)
       (remove (fn [doc]
                 (doc-meta/draft? (load-meta cohort doc))))))

(defn next-doc
  "Creates a new doc for a cohort"
  [cohort]
  (let [number (inc (or (->> (docs cohort)
                             last
                             doc/number)
                        0))]
    (doc/from-slug (str (cohort/slug cohort) "-" number))))

(defn all-cohort+docs []
  (->> (vals cohorts)
       (mapcat (fn [cohort]
                 (for [d (docs cohort)]
                   [cohort d])))
       (remove (fn [[cohort doc]] (doc-meta/draft? (load-meta cohort doc))))))

(defn random-cohort+doc
  "Returns a random tuple of (cohort, doc) from all known cohorts

  All documents have the same probability to be drawn. So cohorts with many
  published documents will be drawn more frequently than cohorts with fewer
  published documents."
  []
  (rand-nth (into [] (all-cohort+docs))))

(comment
  (all-cohort+docs)

  (count (all-cohort+docs)))
