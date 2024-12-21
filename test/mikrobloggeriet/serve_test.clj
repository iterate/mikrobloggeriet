(ns mikrobloggeriet.serve-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [clojure.walk :refer [prewalk]]
   [mikrobloggeriet.db :as db]
   [mikrobloggeriet.serve :as serve]))

(def db (db/loaddb db/cohorts db/authors))

(deftest index-test
  (let [index-resp (serve/index {:mikrobloggeriet.system/datomic db})
        index (:body index-resp)]
    (testing "An index was returned"
      (is (some? index)))

    (testing "Index looks like html"
      (is (and (string? index)
               (str/starts-with? index "<!DOCTYPE"))))

    (testing "Index refers to olorm-4"
      (is (str/includes? index "/olorm/olorm-4")))))

(deftest doc-test
  (let [injected-app (fn [req]
                       (serve/app (assoc req :mikrobloggeriet.system/datomic db)))]
    ;; Sanity test that one document for each cohort renders successfully. Makes
    ;; it more comfortable to work with doc logic!
    (let [olorm-1 (injected-app {:uri "/olorm/olorm-1/" :request-method :get})]
      (is (str/includes? (str/lower-case (:body  olorm-1))
                         "søvn")
          "OLORM-1 handler om viktigheten av en god natts søvn."))

    (let [jals-1 (injected-app {:uri "/jals/jals-1/" :request-method :get})]
      (is (str/includes? (str/lower-case (:body jals-1))
                         "modeller")
          "JALS-1 handler maskinlæringsmodeller."))

    (let [oj-1 (injected-app {:uri "/oj/oj-1/" :request-method :get})]
      (is (str/includes? (str/lower-case (:body oj-1))
                         "refaktorering")
          "OJ-1 handler refaktorering."))))

(defn strip-function-objects
  "Remove function objects from a data structure so that the rest can be compared
  with another using clojure.core/= in tests"
  [x]
  (prewalk (fn [node]
             (if (fn? node)
               nil
               node))
           x))

(deftest markdown-cohort-routes-test
  (is (= (-> (serve/markdown-cohort-routes (:cohort/olorm db/cohorts))
             strip-function-objects)
         ["/olorm"
          ["/" {:get nil, :name :mikrobloggeriet.olorm/all}]
          ["/:slug/" {:get nil}]])))
