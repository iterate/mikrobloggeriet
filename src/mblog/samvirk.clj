(ns mblog.samvirk
  (:refer-clojure :exclude [load])
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]))

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

(defn load []
  (let [style (rand-nth styles)
        theme (rand-nth themes)
        font (rand-nth fonts)
        bg-color (css->background-color (read-theme theme))
        text-color (css->text-color (read-theme theme))]
    {:style style
     :theme theme
     :bg-color bg-color
     :text-color text-color
     :font font}))
