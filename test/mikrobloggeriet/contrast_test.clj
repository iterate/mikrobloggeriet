(ns mikrobloggeriet.contrast-test
  (:require [clojure.test :refer [deftest is]]
            [mikrobloggeriet.contrast :as contrast]
            [clojure.string :as str]))

(deftest relative-luminance
  (is (= 0.0 (contrast/relative-luminance [0 0 0])))
  (is (= 1.0 (contrast/relative-luminance [255 255 255]))))

(deftest luminance
  (is (= 21.0
         (contrast/contrast-ratio (contrast/relative-luminance [0 0 0])
                             (contrast/relative-luminance [255 255 255]))))

  (is (= 21.0 (contrast/rgb->score [0 0 0] [255 255 255])))
  (is (= 1.0 (contrast/contrast-ratio 1 1))))

(deftest hex->rgb
  (is (= [0 0 0] (contrast/hex->rgb "#00000")))
  (is (= [255 255 255] (contrast/hex->rgb "#ffffff"))))

(deftest rgb->hex
  (is (= "#00bfff" (contrast/rgb->hex [0 191 255])))
  (is (= "#ffccff" (contrast/rgb->hex [255 204 255])))
  (let [rand-color (contrast/rand-rgb)]
    (is (= rand-color
           (contrast/hex->rgb (contrast/rgb->hex rand-color))))))

(deftest scoring
  (is (= 21.0 (contrast/rgb->score [0 0 0] [255 255 255]))))

(deftest random-hex-color
  (let [rand-hex (contrast/rand-hex-color)]
    (is (str/starts-with? rand-hex "#"))
    (is (= 7 (count rand-hex)))))

(deftest generate-contrasting-colors
  (let [threshold 7
        colors (contrast/gen-colors threshold)]
    (is (< threshold
           (contrast/rgb->score (contrast/hex->rgb (:c1 colors))
                                (contrast/hex->rgb (:c2 colors)))))))
