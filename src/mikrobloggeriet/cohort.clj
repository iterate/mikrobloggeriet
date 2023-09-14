(ns mikrobloggeriet.cohort
  (:refer-clojure :exclude [name])
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.doc :as doc]
   [clojure.string :as str]))

(comment
  ;; example cohort:
  (sorted-map
   :cohort/root "text/oj"
   :cohort/slug "oj"
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}])
  )

;; This namespace contains no constructors.
;; See store.clj for available cohorts.

(defn slug [cohort]
  (or
   (:cohort/slug cohort)
   ;; TODO: delete this branch, present for backwards compatibility
   (when-let [cohort-id (:cohort/id cohort)]
     (clojure.core/name cohort-id))))

(defn root [cohort]
  (:cohort/root cohort))

(defn members [cohort]
  (:cohort/members cohort))

(defn repo-path [cohort]
  (:cohort/repo-path cohort "."))

(defn set-repo-path [cohort repo-path]
  (assoc cohort :cohort/repo-path repo-path))

(defn name [cohort]
  (some-> (slug cohort) str/upper-case))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DEPRECATED
;;
;; Cohorts are now found in mikrobloggeriet.store.

(def
  ^:deprecated
  olorm
  "Instead: use store/olorm"
  (sorted-map
   :cohort/root "o"
   :cohort/id :olorm
   :cohort/members [{:author/email "git@teod.eu", :author/first-name "Teodor"}
                    {:author/email "lars.barlindhaug@iterate.no", :author/first-name "Lars"}
                    {:author/email "oddmunds@iterate.no", :author/first-name "Oddmund"}
                    {:author/email "richard.tingstad@iterate.no", :author/first-name "Richard"}]))

(def
  ^:deprecated
  jals
  "Instead: use store/jals"
  (sorted-map
   :cohort/root "j"
   :cohort/id :jals
   :cohort/members [{:author/email "aaberg89@gmail.com", :author/first-name "Jørgen"}
                    {:author/email "adrian.tofting@iterate.no",
                     :author/first-name "Adrian"}
                    {:author/email "larshbj@gmail.com", :author/first-name "Lars"}
                    {:author/email "sindre@iterate.no", :author/first-name "Sindre"}]))
