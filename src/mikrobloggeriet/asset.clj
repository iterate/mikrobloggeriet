(ns mikrobloggeriet.asset
  (:require
   [clojure.java.io :as io]
   [ring.util.io :as ring-io]
   [ring.util.mime-type :as ring-mime]
   [ring.util.time :as ring-time])
  (:import
   [java.io File]))

(defn ^:private -find-file ^File [path]
  (let [file ^File (io/file path)]
    (when (.exists file)
      (let [file (if (.isDirectory file)
                   (io/file file "index.html")
                   file)]
        (when (.exists file)
          file)))))

(defn load-asset [path]
  (when-some [file (-find-file path)]
    {:status 200
     :body   file
     :headers {"Content-Length" (.length file)
               "Last-Modified"  (ring-time/format-date (ring-io/last-modified-date file))
               "Content-Type"   (ring-mime/ext-mime-type (.getName file))}}))

(comment
  (load-asset "images/schnauzer.jpg")
  ;; => {:status 200,
  ;;     :body #object[java.io.File 0x7ad71a1c "images/schnauzer.jpg"],
  ;;     :headers
  ;;     {"Content-Length" 157959,
  ;;      "Last-Modified" "Wed, 31 Jul 2024 11:43:11 GMT",
  ;;      "Content-Type" "image/jpeg"}}

  :rcf)
