(ns mblog.indigo-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mblog.indigo :as indigo]
   [mblog.samvirk :as samvirk]
   [mikrobloggeriet.db :as db]))

(deftest lazyload-images
  (is (= [:div [:img {:loading "lazy"}]]
         (indigo/lazyload-images [:div [:img]])))

  (is (= [:div [:img {:loading "lazy"} "body"]]
         (indigo/lazyload-images [:div [:img "body"]])))

  (is (= [:div [:img {:loading "lazy" :class "lol"} "body"]]
         (indigo/lazyload-images [:div [:img {:class "lol"} "body"]]))))

(deftest left-bar
  (is
   (contains?
    (->> {:docs [{:doc/title "Unminifying av kode med LLM"
                  :doc/markdown "lang tekst"}]
          :samvirk (samvirk/load)}
         (indigo/innhold->hiccup)
         (tree-seq seqable?
                   identity)
         set)
    "Unminifying av kode med LLM")))

(def db (db/loaddb {:cohorts db/cohorts :authors db/authors}))

(deftest req->innhold
  (testing "ingen valgt kohort"
    (let [innhold (indigo/req->innhold {:mikrobloggeriet.system/datomic db})
          docs (:docs innhold)
          slugs (->> docs
                     (map :doc/slug)
                     (into (sorted-set)))]
      (is (contains? slugs "olorm-1"))
      (is (contains? slugs "jals-2"))
      (is (nil? (:current-cohort docs)))))
  (testing "valgt OLORM"
    (let [innhold (indigo/req->innhold {:mikrobloggeriet.system/datomic db
                                        :query-params {"cohort" "olorm"}})
          docs (:docs innhold)
          slugs (into (sorted-set) (map :doc/slug docs))]
      (is (contains? slugs "olorm-1"))
      (is (contains? slugs "jals-2"))
      (is (= (-> innhold :current-cohort :cohort/slug) "olorm")))))
