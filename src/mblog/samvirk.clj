(ns mblog.samvirk
  (:refer-clojure :exclude [load])
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [mikrobloggeriet.contrast :as contrast]))

(defn parse-css [re css-str]
  (when-let [s (re-find re css-str)]
    (str/replace (last s) "'" "")))

(def css-re
  {:font #"--main-font:\s*(.*?);"})

(defn css->font [font]
  (parse-css (:font css-re) font))

(defn filenames [path]
  (->> (fs/list-dir path)
       (map fs/file-name)
       (map str)
       sort
       vec))

(def fonts (filenames "public/css/fonts"))

(defn font-path [font]
  (str "css/fonts/" font))

(defn read-font [font-name] (slurp (str "public/css/fonts/" font-name)))

(def css-template
  ":root {
   --first100: rgb(%s);
   --first80: rgba(%s, 0.8);
   --first50: rgba(%s, 0.5);
   --first20: rgba(%s, 0.2);
   --first10: rgba(%s, 0.1);
   --second100: rgb(%s);
   --second80: rgba(%s, 0.8);
   --second50: rgba(%s, 0.5);
   --second20: rgba(%s, 0.2);
   --second10: rgba(%s, 0.1);
}")

(defn load []
  (let [font (rand-nth fonts)
        colors (contrast/gen-colors 5)
        text (:c1 colors)
        bg (:c2 colors)
        text-str (str (text 0) "," (text 1) "," (text 2))
        bg-str (str (bg 0) "," (bg 1) "," (bg 2))]
    {:bg-color (contrast/rgb->hex text)
     :text-color (contrast/rgb->hex bg)
     :font font
     :root (apply format css-template
                  (concat (repeat 5 text-str) (repeat 5 bg-str)))}))
