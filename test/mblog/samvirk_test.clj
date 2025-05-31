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

(deftest infer-main-cont
  (is (= "IBM Plex Mono, monospace"
         (samvirk/infer-main-font css-str-with-font))))

(deftest hydrate-test
  (is (=
       (samvirk/hydrate {:font "font11.css",
                         :colors
                         {:c1 [127 8 163], :c2 [186 210 225], :score 5.431333280225553, :iter 12}})
       {:bg-color "#7f08a3",
        :text-color "#bad2e1",
        :font "font11.css",
        :root
        ":root {\n   --first100: rgb(127,8,163);\n   --first80: rgba(127,8,163, 0.8);\n   --first50: rgba(127,8,163, 0.5);\n   --first20: rgba(127,8,163, 0.2);\n   --first10: rgba(127,8,163, 0.1);\n   --second100: rgb(186,210,225);\n   --second80: rgba(186,210,225, 0.8);\n   --second50: rgba(186,210,225, 0.5);\n   --second20: rgba(186,210,225, 0.2);\n   --second10: rgba(186,210,225, 0.1);\n}"}))
  )
