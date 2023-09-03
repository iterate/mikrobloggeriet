(ns mikrobloggeriet.doc-meta)

(defn draft? [doc+meta]
  (= :draft
     (:doc/state doc+meta)))

(comment
  (draft? {:doc/state :draft}))
