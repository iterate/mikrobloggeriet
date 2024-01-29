(ns mikrobloggeriet.doc-meta)

(defn draft? [doc+meta]
  (= :draft
     (:doc/state doc+meta)))

(defn created [doc+meta]
  (:doc/created doc+meta))

(defn created-instant [doc+meta]
  (.toInstant (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") (created doc+meta))))

(comment
  (draft? {:doc/state :draft}))
