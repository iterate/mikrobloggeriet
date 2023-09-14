(ns mikrobloggeriet.store-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.store :as store]
   [mikrobloggeriet.doc :as doc]
   [mikrobloggeriet.cohort :as cohort]))

(deftest doc-exists?-test
  (is (store/doc-exists? store/olorm
                         (doc/from-slug "olorm-1"))))

(deftest doc-href-test
  (is (= "/olorm/olorm-1/"
         (store/doc-href store/olorm (doc/from-slug "olorm-1")))))



(store/doc-md-path store/olorm (doc/from-slug "olorm-1"))

(store/doc-md-path (cohort/set-repo-path store/olorm "/TESTROOT")
                   (doc/from-slug "olorm-1"))

(deftest doc-md-path-test
  (let [cohort (cohort/set-repo-path store/olorm "/TESTFOLDER")]
    (is (= (str "/TESTFOLDER/"
                (cohort/root cohort)
                "/olorm-1/index.md")
           (str (store/doc-md-path (cohort/set-repo-path store/olorm "/TESTFOLDER")
                                   (doc/from-slug "olorm-1")))))))
