(ns mikrobloggeriet.doc-meta-test
  (:require
   [clojure.test :refer [deftest is]]
   [mikrobloggeriet.doc-meta :as doc-meta]))

(deftest draft?-test
  (is (doc-meta/draft? {:doc/state :draft}))
  (is (not (doc-meta/draft? {}))))
