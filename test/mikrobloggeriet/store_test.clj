(ns mikrobloggeriet.store-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.store :as store]
   [mikrobloggeriet.doc :as doc]))

(deftest doc-exists?-test
  (is (store/doc-exists? store/olorm
                         (doc/from-slug "olorm-1"))))

(deftest doc-href-test
  (is (= "/olorm/olorm-1/"
         (store/doc-href store/olorm (doc/from-slug "olorm-1")))))
