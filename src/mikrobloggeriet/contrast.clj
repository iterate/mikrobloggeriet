(ns mikrobloggeriet.contrast
  (:require
   [clojure.math :as math]
   [clojure.string :as str]))

; Rewritten from: https://github.com/tmcw/relative-luminance/blob/master/index.js

; # Relative luminance
; http://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
; https://en.wikipedia.org/wiki/Luminance_(relative)
; https://en.wikipedia.org/wiki/Luminosity_function
; https://en.wikipedia.org/wiki/Rec._709#Luma_coefficients

; red, green, and blue coefficients
(def rc 0.2126)
(def gc 0.7152)
(def bc 0.0722)

; low-gamma adjust coefficient
(def lowc (/ 1 12.92))

(defn adjust-gamma [_]
  (math/pow (/ (+ _ 0.055) 1.055) 2.4))

(defn relative-luminance
  "The relative brightness of any point in a colorspace, normalized to 0 for darkest black and 1 for lightest white"
  [[r g b]]
  (let [rsrgb (/ r 255)
        gsrgb (/ g 255)
        bsrgb (/ b 255)
        r (if (<= rsrgb 0.03923) (* rsrgb lowc) (adjust-gamma rsrgb))
        g (if (<= gsrgb 0.03923) (* gsrgb lowc) (adjust-gamma gsrgb))
        b (if (<= bsrgb 0.03923) (* bsrgb lowc) (adjust-gamma bsrgb))]
    (+ (* r rc) (* g gc) (* b bc))))

(defn contrast-ratio [a b]
  (let [l1 (max a b)
        l2 (min a b)]
    (/ (+ l1 0.05) (+ l2 0.05))))

(defn rgb->score
  "Calculate the contrast ratio between two relative luminance values"
  [c1 c2]
  (contrast-ratio (relative-luminance c1) (relative-luminance c2)))

(defn rgb->hex [[r g b]]
  (format "#%02x%02x%02x" r g b))

(defn rand-rgb []
  [(rand-int 256) (rand-int 256) (rand-int 256)])

(defn gen-colors
  "Generate two random colors with a contrast radio over a given threshold.
   Contrast ratios can range from 1 to 21.
   To ensure performance, the the threshold is capped at 18."
  [threshold]
  (if (> threshold 18) nil
      (loop [c1 (rand-rgb)
             c2 (rand-rgb)
             iter 0]
        (let [score (rgb->score c1 c2)]
          (if (> score threshold)
            {:c1 c1
             :c2 c2
             :score score
             :iter iter}
            (recur c2 (rand-rgb) (inc iter)))))))

(defn rand-hex-color []
  (str "#" (format "%06x" (rand-int 16777215))))

(defn hex->rgb [hex]
  (let [hex (str/replace hex #"^#" "")
        n (Integer/parseInt hex 16)
        red (bit-shift-right n 16)
        green (bit-and (bit-shift-right n 8) 255)
        blue (bit-and n 255)]
    [red green blue]))
