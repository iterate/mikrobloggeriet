(ns mikrobloggeriet.teodor.last-changed
  (:require
   [babashka.fs :as fs]))


(->>
 (fs/glob "." "**/*.{js,html,clj,md,edn}")
 (map fs/last-modified-time)
 (map fs/file-time->millis)
 (reduce max)
 fs/millis->file-time)
;; => (#object[java.nio.file.attribute.FileTime 0x79af5549 "2024-01-12T21:23:01.321554243Z"]
;;     #object[java.nio.file.attribute.FileTime 0x19875a94 "2024-01-30T20:54:39.24684365Z"]
;;     #object[java.nio.file.attribute.FileTime 0x27b75483 "2024-02-11T11:55:36.44456266Z"])

(->>
 (fs/glob "." "**/*.{js,html,clj,md,edn}")
 (map fs/last-modified-time)
 (map fs/file-time->millis)
 (reduce max)
 fs/millis->file-time)
;; => #object[java.nio.file.attribute.FileTime 0x43aeb233 "2024-02-11T12:36:33.663Z"]


(apply max-key
       (comp fs/file-time->millis fs/last-modified-time)
       (fs/glob "." "**/*.{js,css,html,clj,md,edn}"))
;; => #object[sun.nio.fs.UnixPath 0xb1a81e "draft/mikrobloggeriet/teodor/last_changed.clj"]
