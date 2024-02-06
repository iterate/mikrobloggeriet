(ns mikrobloggeriet.relative-luminance
  (:require [clojure.math :as math]))

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

(relative-luminance [0 0 0])



;; /**
;;  * Given a 3-element array of R, G, B varying from 0 to 255, return the luminance
;;  * as a number from 0 to 1.
;;  * @param {Array<number>} rgb 3-element array of a color
;;  * @returns {number} luminance, between 0 and 1
;;  * @example
;;  * var luminance = require('relative-luminance');
;;  * var black_lum = luminance([0, 0, 0]); // 0
;;  */
;; export default function relativeLuminance(rgb) {
;;   const rsrgb = rgb[0] / 255;
;;   const gsrgb = rgb[1] / 255;
;;   const bsrgb = rgb[2] / 255;

;;   const r = rsrgb <= 0.03928 ? rsrgb * lowc : adjustGamma(rsrgb);
;;   const g = gsrgb <= 0.03928 ? gsrgb * lowc : adjustGamma(gsrgb);
;;   const b = bsrgb <= 0.03928 ? bsrgb * lowc : adjustGamma(bsrgb);

;;   return r * rc + g * gc + b * bc;
;; }


