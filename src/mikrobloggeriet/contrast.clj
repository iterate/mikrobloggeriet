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

(defn relative-luminance [rgb]
  (let [rsrgb (/ (rgb 0) 255)
        gsrgb (/ (rgb 1) 255)
        bsrgb (/ (rgb 2) 255)
        r (if (<= rsrgb 0.03923) (* rsrgb lowc) (adjust-gamma rsrgb))
        g (if (<= gsrgb 0.03923) (* gsrgb lowc) (adjust-gamma gsrgb))
        b (if (<= bsrgb 0.03923) (* bsrgb lowc) (adjust-gamma bsrgb))]
    (+ (* r rc) (* g gc) (* b bc))))

(defn luminance [a b]
  (let [l1 (max a b)
        l2 (min a b)]
    (/ (+ l1 0.05) (+ l2 0.05))))

(defn rbg->score [a b]
  (luminance (relative-luminance a) (relative-luminance b)))

;; Get the contrast ratio between two relative luminance values
(defn hex->rgb [hex]
  (let [hex (str/replace hex #"^#" "")
        n (Integer/parseInt hex 16)
        red (bit-shift-right n 16)
        green (bit-and (bit-shift-right n 8) 255)
        blue (bit-and n 255)]
    [red green blue]))

(defn hex->score [a b]
  (rbg->score (hex->rgb a) (hex->rgb b)))

(defn random-hex-color []
  (str "#" (format "%06x" (rand-int 16777215))))

(defn generate-colors [threshold]
  (let [color-1 (random-hex-color)
        color-2 (random-hex-color)
        score (hex->score color-1 color-2)]
    (if (> score threshold)
      {:color-1 color-1
       :color-2 color-2
       :score score}
      (recur threshold))))
