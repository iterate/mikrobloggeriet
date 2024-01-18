(ns mikrobloggeriet.urlog-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.urlog :as urlog]))

(deftest select-door-test
  (let [doors (:doors (urlog/load-ascii-assets urlog/assets-dir))
        url-1 "https://mikrobloggeriet.no"
        url-2 "https://iterate.no"
        url-3 "https://wikipedia.org"]
    (testing "when the URL is constant, the door is the same"
      (is (= (urlog/select-door url-1 doors)
             (urlog/select-door url-1 doors)
             (urlog/select-door url-1 doors))))
    (testing "for these three URLS, select-door shall give different URLs"
      (is (not (= (urlog/select-door url-1 doors)
                  (urlog/select-door url-2 doors)
                  (urlog/select-door url-3 doors)))))))
