(ns mikrobloggeriet.contrast
  (:require
   [clojure.string :as str]
   [mikrobloggeriet.relative-luminance :as relative-luminance]
   [nextjournal.clerk :as clerk]))

(defn hex->rgb [hex]
  (let [hex (str/replace hex #"^#" "")
        n (Integer/parseInt hex 16)
        red (bit-shift-right n 16)
        green (bit-and (bit-shift-right n 8) 255)
        blue (bit-and n 255)]
    [red green blue]))

(comment
  (hex->rgb "#000000"))

;; Get the contrast ratio between two relative luminance values
(defn luminance [a b]
  (let [l1 (max a b)
        l2 (min a b)]
    (/ (+ l1 0.05) (+ l2 0.05))))

(comment
  (luminance 1 1))


(defn rbg->score [a b]
  (luminance (relative-luminance/relative-luminance a) (relative-luminance/relative-luminance b)))

(comment
  (rbg->score [0 0 0] [255 255 255]))

(defn hex->score [a b]
  (rbg->score (hex->rgb a) (hex->rgb b)))

(comment
  (hex->score "#008080" "#f9ffd0")
  (hex->score "#ffe000" "#000000"))

;; Source: https://www.paulirish.com/2009/random-hex-color-code-snippets/
;; (defn random-hex-color []
;;   (str "#" (Long/toHexString (math/floor (* (math/random) 16777215)))))


(defn random-hex-color []
  (str "#" (format "%06x" (rand-int 16777215))))

(random-hex-color)

(hex->score (random-hex-color) (random-hex-color))

;; (defn random-hex-color []
;;   (let [hex (apply str (repeatedly 6 #(rand-nth "0123456789ABCDEF")))]
;;     (str "#"
;;          (if (= 6 (count hex)) hex (str "0" hex)))))

(random-hex-color)

(defn generate-contrasting-colors [threshold]
  (loop [color-1 (random-hex-color)
         color-2 (random-hex-color)
         score (hex->score color-1 color-2)]
    (if (> score threshold)
      {:color-1 color-1
       :color-2 color-2
       :score score}
      (recur (random-hex-color) (random-hex-color) (hex->score color-1 color-2)))))

(comment
  (generate-contrasting-colors 4.5))