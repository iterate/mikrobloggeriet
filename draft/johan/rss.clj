(ns johan.rss
  ( :require [clj-rss.core :as rss ])
  )

(defn -main [& _args]
  (println (rss/channel-xml {:title "Foo" :link "http://localhost:7223/feed" :description "some channel"}
                            {:title "Foo"}
                            {:title "post" :author "author@foo.bar"}
                            {:description "bar"}
                            {:description "baz" "content:encoded" "Full content"})))

(rss/channel-xml {:title "oj-1" :link "https://mikrobloggeriet.no/oj/oj-1" :description "et blogginnlegg på OJ jeg er ny"}
                 {:title "oj-2" :link "https://mikrobloggeriet.no/oj/oj-2" :description "et blogginnlegg på OJ jeg er ny"}
                 {:title "oj-3" :link "https://mikrobloggeriet.no/oj/oj-3" :description "et blogginnlegg på OJ jeg er ny"}
                 {:title "Foo"}
                 {:title "post" :author "author@foo.bar"}
                 {:description "bar"}
                 {:description "baz" "content:encoded" "Full content"})
              