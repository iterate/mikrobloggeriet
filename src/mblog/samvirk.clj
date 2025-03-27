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
  {:bg #"--background01:\s*(.*?);"
   :text #"--white100:\s*(.*?);"
   :font #"--main-font:\s*(.*?);"})

(defn css->background-color [theme]
  (parse-css (:bg css-re) theme))

(defn css->text-color [theme]
  (parse-css (:text css-re) theme))

(defn css->font [font]
  (parse-css (:font css-re) font))

(defn filenames [path]
  (->> (fs/list-dir path)
       (map fs/file-name)
       (map str)
       sort
       vec))

(def styles (filenames "public/css/styles"))
(def themes (filenames "public/css/themes"))
(def fonts (filenames "public/css/fonts"))

(defn style-path [style]
  (str "css/styles/" style))

(defn theme-path [theme]
  (str "css/themes/" theme))

(defn font-path [font]
  (str "css/fonts/" font))

(defn read-theme [theme-name] (slurp (str "public/css/themes/" theme-name)))
(defn read-style [style-name] (slurp (str "public/css/styles/" style-name)))
(defn read-font [font-name] (slurp (str "public/css/fonts/" font-name)))

(css->font (read-font "font1.css"))

(def css-template
  ":root {
   --white100: rgb(%s);
   --white80: rgba(%s, 0.8);
   --white50: rgba(%s, 0.5);
   --white20: rgba(%s, 0.2);
   --white10: rgba(%s, 0.1);
   --black100: rgb(%s);
   --black80: rgba(%s, 0.8);
   --black50: rgba(%s, 0.5);
   --black20: rgba(%s, 0.2);
   --black10: rgba(%s, 0.1);
   --background: rgb(%s);
}")

(defn load []
  (let [style (rand-nth styles)
        theme (rand-nth themes)
        font (rand-nth fonts)
        colors (contrast/gen-colors 5)
        text (:c1 colors)
        bg (:c2 colors)
        text-str (str (text 0) "," (text 1) "," (text 2))
        bg-str (str (bg 0) "," (bg 1) "," (bg 2))]
    {:style style
     :theme theme
     :bg-color (contrast/rgb->hex text)
     :text-color (contrast/rgb->hex bg)
     :font font
     :root (apply format css-template
                  (concat (repeat 5 text-str) (repeat 6 bg-str)))}))

