;; # Cohort: a missing Mikrobloggeriet abstraction

^{:nextjournal.clerk/visibility {:code :hide}}
(ns mikrobloggeriet.teodor.cohort
  (:require
   [clojure.pprint :refer [pprint]]
   [babashka.fs :as fs]))

;; Currently, adding new cohorts requires adding lots of code.
;; We have several cohort specific namespaces:

[`mikrobloggeriet.olorm
 `mikrobloggeriet.olorm-cli
 `mikrobloggeriet.jals
 `mikrobloggeriet.jals-cli]

;; We want to make it easy to add new cohorts.
;;
;; We propose moving tools for working with cohorts `mikrobloggeriet.cohort`,
;; along with cohort data (what a cohort is about, who is in a cohort).
;;
;; This document explores the current state of the cohort api.

(def olorm
  {:cohort/root "o"
   :cohort/id :olorm
   :cohort/members [{:author/email "git@teod.eu", :author/first-name "Teodor"}
                    {:author/email "lars.barlindhaug@iterate.no", :author/first-name "Lars"}
                    {:author/email "oddmunds@iterate.no", :author/first-name "Oddmund"}
                    {:author/email "richard.tingstad@iterate.no", :author/first-name "Richard"}]})

(def jals
  {:cohort/root "j"
   :cohort/id :jals
   :cohort/members [{:author/email "aaberg89@gmail.com", :author/first-name "Jørgen"}
                    {:author/email "adrian.tofting@iterate.no",
                     :author/first-name "Adrian"}
                    {:author/email "larshbj@gmail.com", :author/first-name "Lars"}
                    {:author/email "sindre@iterate.no", :author/first-name "Sindre"}]})

(def oj
  {:cohort/root "text/oj"
   :cohort/id :oj
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}]})

(def genai
  {:cohort/root "text/genai"
   :cohort/id :genai
   :cohort/members [{:author/first-name "Julian"}]})

(defn doc? [cohort doc]
  (and (:doc/slug doc)
       (fs/directory? (fs/file (:cohort/root cohort)
                               (:doc/slug doc)))
       (fs/exists? (fs/file (:cohort/root cohort)
                            (:doc/slug doc)
                            "meta.edn"))))

(defn docs [cohort]
  (let [id (:cohort/id cohort)
        root (:cohort/root cohort)]
    (assert id)
    (assert root)
    (assert (fs/directory? root))
    (->> (fs/list-dir (fs/file root))
         (map (fn [f]
                {:doc/slug (fs/file-name f)}))
         (filter (partial doc? cohort)))))

(docs olorm)

;; is this for use from the server, or from the CLI?
;; perhaps the CLI just needs to know where the repo is.
;; let's go for that.

;; let's try listing the public interface of olorm, then implement each function.

['->olorm
 'author-name
 'coerce
 'docs :DONE
 'exists?
 'git-user-email
 'href
 'index-md-path
 'infer-created-date
 'load-meta
 'md-skeleton
 'meta-path
 'next-number
 'olorms :DONE
 'path
 'random
 'save-meta!
 'today
 'uuid
 'validate]
