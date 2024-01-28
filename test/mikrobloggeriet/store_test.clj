(ns mikrobloggeriet.store-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.store :as store]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.cohort.markdown :as cohort]))

(deftest doc-exists?-test
  (is (store/doc-exists? store/olorm
                         (doc/from-slug "olorm-1"))))

(deftest doc-href-test
  (is (= "/olorm/olorm-1/"
         (store/doc-href store/olorm (doc/from-slug "olorm-1")))))

(deftest doc-md-path-test
  (let [cohort (cohort/set-repo-path store/olorm "/TESTFOLDER")]
    (is (= (str "/TESTFOLDER/"
                (cohort/root cohort)
                "/olorm-1/index.md")
           (str (store/doc-md-path (cohort/set-repo-path store/olorm "/TESTFOLDER")
                                   (doc/from-slug "olorm-1")))))))

(deftest docs-test
  (testing "We find olorm-3"
    (is (contains? (->> (store/docs store/olorm)
                        (map doc/slug)
                        (into #{}))
                   "olorm-3")))

  (testing "We find jals-4"
    (is (contains? (->> (store/docs store/jals)
                        (map doc/slug)
                        (into #{}))
                   "jals-4"))))
