(ns mikrobloggeriet.timemachine-test
  (:require [babashka.fs :as fs]
            [clojure.test :refer [deftest is testing]]
            [mikrobloggeriet.timemachine :as timemachine]))

(deftest rev-parse
  (testing "short commit SHAs expand into long ones"
    (is (= (timemachine/rev-parse "." "5e7772e")
           "5e7772ee82bf67b2087cbe3e06629174ab68de28")))

  (testing "refs like HEAD and branch names are supported"
    (is (= (count (timemachine/rev-parse "." "5e7772e"))
           (count (timemachine/rev-parse "." "HEAD"))
           (count (timemachine/rev-parse "." "master")))))
  )

(deftest worktree-add-remove
  (let [tempdir (fs/create-temp-dir)
        sha "5e7772ee82bf67b2087cbe3e06629174ab68de28"
        repo-dir "."
        worktree-dir (str (fs/file tempdir sha))
        worktree-list-dir #(when (fs/exists? worktree-dir)
                             (fs/list-dir worktree-dir))]
    (testing "At first, there are no files in the worktree folder"
      (is (empty? (worktree-list-dir))))

    (testing "After worktree-add, we can find our README in the worktree folder"
      (timemachine/worktree-add repo-dir worktree-dir sha)
      (try
        (is (contains? (set (map fs/file-name (worktree-list-dir)))
                       "README.md"))
        (finally
          (timemachine/worktree-remove repo-dir worktree-dir))))

    (testing "After worktree-remove, the folder is empty."
      (is (empty? (worktree-list-dir))))))

(deftest do-at
  (testing "We can run functions in the past"
    (is (= (timemachine/do-at "HEAD" (constantly ::result))
           ::result)))

  (testing "bb.edn existed at this point in time"
    (is (contains? (timemachine/do-at "5e7772ee82bf67b2087cbe3e06629174ab68de28"
                     (fn [dir]
                       (->> (fs/list-dir dir)
                            (map fs/file-name)
                            (into (sorted-set)))))
                   "bb.edn")))
  )
