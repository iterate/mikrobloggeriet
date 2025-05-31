(ns mblog.samvirk-test
  (:require
   [clojure.test :refer [deftest is]]
   [mblog.samvirk :as samvirk]))

(def css-str-with-font ":root {
    --second10001: #080D92;
    --first100: #fff;
    --first80: rgba(255, 255, 255, 0.8);
    --second100: #000;
    --main-font: 'IBM Plex Mono', monospace;
  }
     h1, h2 {
     color: var(--first100);
     font-style: normal;
     font-family: var(--main-font);
     font-size: clamp(1rem, 1vw, 1.5rem);
     line-height: 150%;}")

(deftest parse-css
  (is (= "IBM Plex Mono, monospace"
         (samvirk/infer-main-font css-str-with-font))))
