(ns mikrobloggeriet.doc-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [datomic.api :as d]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.doc :as doc]))

(deftest created-date
  (is (= "2025-03-19"
         (doc/created-date {:doc/created "2025-03-19T17:45:20Z"}))))

(deftest number-test
  (is (= 1 (doc/number {:doc/slug "olorm-1"})))
  (is (= 42 (doc/number {:doc/slug "jals-42"})))
  (is (nil? (doc/number {:doc/slug "YO-PEEPS"}))))

(deftest doc-href-test
  (is (= "/olorm/olorm-13/"
         (doc/href {:doc/slug "olorm-13"
                    :doc/cohort {:cohort/slug "olorm"}}))))

(def db (db/loaddb {:cohorts db/cohorts :authors db/authors}))

(deftest previous-test
  (is (= "olorm-1"
         (:doc/slug (doc/previous db (d/entity db [:doc/slug "olorm-2"]))))))

(deftest next-test
  (is (= "olorm-3"
         (:doc/slug (doc/next db (d/entity db [:doc/slug "olorm-2"]))))))

(deftest author-first-name-test
  (let [olorm-2 (d/entity db [:doc/slug "olorm-2"])]
    (is (= "Oddmund"
           (doc/author-first-name db olorm-2)))))

(comment
  (into {} (d/entity db [:author/email "git@teod.eu"]))
  (:git.user/email (d/entity db [:doc/slug "olorm-2"]))
  (into {} (d/entity db [:author/email "oddmunds@iterate.no"])))

(deftest title-test
  (is (= "Funksjonell programmering"
         (doc/title {:doc/markdown "# Funksjonell programmering"})))
  (is (= "Funksjonell programmering"
         (doc/title {:doc/title "Funksjonell programmering"}))))

(deftest description-test
  (is (= "Mindre er ofte bedre."
         (doc/description {:doc/markdown "# Funksjonell programmering

Mindre er ofte bedre."}))))

(deftest allows-for-empty-titles
  (is (nil? (doc/title {})))
  (is (nil? (doc/cleaned-title {}))))

(deftest remove-cohort-prefix
  (testing "leaves clean titles as is"
    (is (= (doc/cleaned-title {:doc/title "Funksjonell programmering"})
           "Funksjonell programmering")))

  (testing "removes cohort prefix"
    (is (= (doc/remove-cohort-prefix "NILS-1: Funksjonell programmering")
           "Funksjonell programmering"))
    (is (= (doc/remove-cohort-prefix "NILS-2 - Funksjonell programmering")
           "Funksjonell programmering")))

  (testing "does not the title"
    (is (= (doc/remove-cohort-prefix "OJ JEG ER EN TEST")
           "OJ JEG ER EN TEST"))
    (is (= (doc/remove-cohort-prefix "OLORM-45: --scale i Docker Compose")
           "--scale i Docker Compose"))))

(deftest html-test
  (is (str/includes? (doc/html {:doc/markdown "# Funksjonell programmering"})
                     "programmering"))
  (is (str/includes? (doc/html {:doc/markdown "# Funksjonell programmering"})
                     "<h1")))

(deftest all-test
  (is (contains? (->> (doc/all db)
                      (map :doc/slug)
                      (into #{}))
                 "olorm-1")))

(deftest latest-test
  (is (= (->> (doc/latest db)
              (take-last 3)
              (map :doc/slug))
         '("olorm-3" "olorm-2" "olorm-1"))))

(deftest random-doc-test
  (is (contains? (doc/random-published db)
                 :doc/slug)))
