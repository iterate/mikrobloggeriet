(ns mikrobloggeriet.cohort
  (:require
   [babashka.fs :as fs]))

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
