(ns mikrobloggeriet.cohort
  (:refer-clojure :exclude [name])
  (:require
   [babashka.fs :as fs]
   [mikrobloggeriet.doc :as doc]))

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

(def
  ^:deprecated
  oj
  "Use instead: store/oj"
  (sorted-map
   :cohort/root "text/oj"
   :cohort/id :oj
   :cohort/members [{:author/first-name "Johan"}
                    {:author/first-name "Olav"}]))

(def
  ^:deprecated
  genai
  "Use instead: store/genai"
  (sorted-map
   :cohort/root "text/genai"
   :cohort/id :genai
   :cohort/members [{:author/first-name "Julian"}]))

(def
  ^:deprecated
  cohorts
  "Use instead: store/cohorts"
  (->> [olorm jals oj genai]
       (map (fn [c]
              [(:cohort/id c) c]))
       (into (sorted-map))))

(defn
  ^:deprecated
  docs
  "Use instead: store/docs"
  ([]
   (apply concat
          (for [c (vals cohorts)]
            (->> (docs c)
                 (map (fn [doc]
                        (assoc doc :cohort/id (:cohort/id c))))))))
  ([cohort]
   (let [id (:cohort/id cohort)
         root (:cohort/root cohort)]
     (when (and id root (fs/directory? root))
       (->> (fs/list-dir (fs/file root))
            (map (fn [f]
                   {:doc/slug (fs/file-name f)}))
            (filter (partial doc/exists? cohort))
            (sort-by doc/number))))))

(comment
  ;; jals documents
  (docs jals)

  ;; all documents
  (docs)
  )


(defn slug [cohort]
  (when-let [id (:cohort/id cohort)]
    (clojure.core/name id)))
