(ns mikrobloggeriet.urlog-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [mikrobloggeriet.urlog :as urlog]))

(deftest parse-urlfile-test
  (testing "urls can be parsed"
    (let [oneurl "https://teod.eu"]
      (is (= ["https://teod.eu"]
             (urlog/parse-urlfile oneurl))))
    (let [twourls "https://teod.eu\nhttps://teodorheggelund.com"]
      (is (= ["https://teod.eu"
              "https://teodorheggelund.com"]
             (urlog/parse-urlfile twourls)))))
  (testing "empty lines are whitespace"
    (let [twourls "\n\n\nhttps://teod.eu\n\n\nhttps://teodorheggelund.com\n\n\n"]
      (is (= ["https://teod.eu"
              "https://teodorheggelund.com"]
             (urlog/parse-urlfile twourls)))))
  (testing "comments are whitespace"
    (let [twourls "
# Hjemmeside
https://teod.eu

# Hjemmeside 2
https://teodorheggelund.com

# Alt bortsett fra url-ene (som ikke har #) blir borte!
"]
      (is (= ["https://teod.eu"
              "https://teodorheggelund.com"]
             (urlog/parse-urlfile twourls))))))
