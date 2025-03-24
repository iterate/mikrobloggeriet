(ns mblog.samvirk
  (:refer-clojure :exclude [load])
  (:require [clojure.string :as str]
            [babashka.fs :as fs]))

(defn parse-css [re css-str]
  (when-let [s (re-find re css-str)]
    (str/replace (last s) "'" "")))

(def css-re
  {:bg #"--background01:\s*(.*?);"
   :text #"--white100:\s*(.*?);"
   :font #"font-family:\s*(.*?);"})

(defn css->background-color [theme]
  (parse-css (:bg css-re) theme))

(defn css->text-color [theme]
  (parse-css (:text css-re) theme))

(defn css->font [style]
  (parse-css (:font css-re) style))

(defn filenames [path]
  (->> (fs/list-dir path)
       (map fs/file-name)
       (map str)
       sort
       vec))

(def styles (filenames "public/css/styles"))
(def themes (filenames "public/css/themes"))

(defn style-path [style]
  (str "css/styles/" style))

(defn theme-path [theme]
  (str "css/themes/" theme))

(defn read-theme [theme-name] (slurp (str "public/css/themes/" theme-name)))
(defn read-style [style-name] (slurp (str "public/css/styles/" style-name)))

(defn load []
  (let [style (rand-nth styles)
        theme (rand-nth themes)
        bg-color (css->background-color (read-theme theme))
        text-color (css->text-color (read-theme theme))
        font (css->font (read-style style))]
    {:style style
     :theme theme
     :bg-color bg-color
     :text-color text-color
     :font font}))
