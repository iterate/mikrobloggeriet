(ns mikrobloggeriet.contrast-test
  (:require [clojure.test :refer [deftest is]]
            [mikrobloggeriet.contrast :as contrast]
            [clojure.string :as str]))

(deftest relative-luminance
  (is (= 0.0 (contrast/relative-luminance [0 0 0])))
  (is (= 1.0 (contrast/relative-luminance [255 255 255]))))

(deftest luminance
  (is (= 21.0
         (contrast/luminance (contrast/relative-luminance [0 0 0])
                             (contrast/relative-luminance [255 255 255]))))

  (is (= 21.0 (contrast/rbg->score [0 0 0] [255 255 255])))

  (is (= 1.0 (contrast/luminance 1 1))))

(deftest hex->rgb
  (is (= [0 0 0] (contrast/hex->rgb "#00000")))
  (is (= [255 255 255] (contrast/hex->rgb "#ffffff"))))

(deftest scoring
  (is (= 21.0 (contrast/rbg->score [0 0 0] [255 255 255])))
  (is (= 21.0 (contrast/hex->score "#00000" "#ffffff"))))

(deftest random-hex-color
  (let [rand-hex (contrast/rand-hex-color)]
    (is (str/starts-with? rand-hex "#"))
    (is (= 7 (count rand-hex)))))

(deftest generate-contrasting-colors
  (let [threshold 7
        colors (contrast/gen-colors threshold)]
    (is (< 7 (contrast/hex->score (:c1 colors) (:c2 colors))))))
