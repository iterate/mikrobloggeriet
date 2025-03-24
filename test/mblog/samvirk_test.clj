(ns mblog.samvirk-test
  (:require [clojure.test :refer [deftest is]]
            [mblog.samvirk :as samvirk]))

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
           (samvirk/css->font css-str)))

    (is (= "#080D92"
           (samvirk/css->background-color css-str)))

    (is (= "#fff"
           (samvirk/css->text-color css-str)))

    (is (nil? (samvirk/css->text-color "ugyldig css")))))
