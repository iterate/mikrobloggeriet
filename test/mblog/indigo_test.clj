(ns mblog.indigo-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.indigo :as indigo]))

(deftest lazyload-images
  (is (= [:div [:img {:loading "lazy"}]]
         (indigo/lazyload-images [:div [:img]])))

  (is (= [:div [:img {:loading "lazy"} "body"]]
         (indigo/lazyload-images [:div [:img "body"]])))

  (is (= [:div [:img {:loading "lazy" :class "lol"} "body"]]
         (indigo/lazyload-images [:div [:img {:class "lol"} "body"]])))
  )

(deftest parse-css
  (let [css-str ":root {
    --background01: #080D92;
    --white100: #fff;
    --white80: rgba(255, 255, 255, 0.8);
    --white50: rgba(255, 255, 255, 0.5);
    --white20: rgba(255, 255, 255, 0.2);
    --white10: rgba(255, 255, 255, 0.1);
    --black100: #000;
    --black80: rgba(0, 0, 0, 0.8);
    --black50: rgba(0, 0, 0, 0.5);
    --black20: rgba(0, 0, 0, 0.2);
    --black10: rgba(0, 0, 0, 0.1);
  }

     h1, h2, h3, h4, h5, h6, p, li, figcaption, aside {
     color: var(--white100);
     text-decoration: none;
     font-family: 'IBM Plex Mono', monospace;
     font-weight: 300;
     font-style: normal;
     font-size: clamp(1rem, 1vw, 1.5rem);
     line-height: 150%;}"]
    (is (= "IBM Plex Mono, monospace"
           (indigo/parse-css (:font indigo/css-re) css-str)))
    (is (= "#080D92"
           (indigo/parse-css (:bg indigo/css-re) css-str)))
    (is (= "#fff"
           (indigo/parse-css (:text indigo/css-re) css-str)))))


(deftest left-bar
  (is
   (contains?
    (->> [{:doc/title "Unminifying av kode med LLM"
           :doc/markdown "lang tekst"}]
         (indigo/innhold->hiccup)
         (tree-seq seqable?
                   identity)
         set)
    "Unminifying av kode med LLM")))
