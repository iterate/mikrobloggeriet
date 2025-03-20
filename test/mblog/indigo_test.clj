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
    --black100: #000;
  }
     h1, h2 {
     color: var(--white100);
     font-style: normal;
     font-family: 'IBM Plex Mono', monospace;
     font-size: clamp(1rem, 1vw, 1.5rem);
     line-height: 150%;}"]

    (is (= "IBM Plex Mono, monospace"
           (indigo/css->font css-str)))

    (is (= "#080D92"
           (indigo/css->background-color css-str)))

    (is (= "#fff"
           (indigo/css->text-color css-str)))

    (is (nil? (indigo/css->text-color "ugyldig css")))))

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
