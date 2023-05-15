;; # Mikrobloggeriet API
;;
;; State: **DESIGN DRAFT**.
;; I'm working on vocabulary and design.
;; I don't want to abstract before I'm confident about the abstraction.
;;
;; An initiative for shared intent and  shared sense of quality among Iterate developers.
;; View this namespace with [Clerk] for a nice reading experience.
;;
;; [Clerk]: https://clerk.vision/

(ns mikrobloggeriet.api
  {:nextjournal.clerk/toc true})

;; ## Mikrobloggeriet client
;;
;; A `client` is like a database connection.
;; It lets you:
;;
;; 1. Query for existing documents
;; 2. Add a new document

;; This is a valid client:

{:nextjournal.clerk/visibility {:code :fold}}

{:repo-path "/home/teodorlu/dev/iterate/olorm"
 :document-dir "o"
 :document-prefix "olorm-"}

;; 2023-05-15: I feel like I want to replace "client" with "cohort".
;; It's actually a domain thing.

;; ## Mikrobloggeriet document
;;
;; The purpose of Mikrobloggeriet is to write, read and share documents.
;;
;; A doc has a slug (eg `"olorm-42"`), a number (eg `42`) or both.
;; This is a valid doc:


{:slug "olorm-42"
 :number 42}

;; ## Calling convention
;;
;; We follow [next.jdbc], and take the client (connection) as the first argument in all functions.
;;
;; [next.jdbc]: https://cljdoc.org/d/com.github.seancorfield/next.jdbc/1.3.874/doc/readme
;;
;; For example:

{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(declare ->client)
(declare ->html)

{:nextjournal.clerk/visibility {:code :show :result :show}}

(let [client (->client {:repo-path ".."
                        :doc-dir "o"
                        :doc-prefix "olorm-"})]
  (->html client {:slug "olorm-42"}))

;; ## TODO write the code

(defn ->client [opts]
  ;; todo validate
  opts)
(defn ->html [client doc]
  ;; todo actually make html
  )

;; ## New proposed vocabulary

{:nextjournal.clerk/visibility {:result :hide}}

;; a _cohort_ is a group of people who are writing together

{:cohort/name "OLORM"                                  ;; user facing name (unique)
 :cohort/ident :olorm                                  ;; identifier (unique)
 :cohort/repo-subdir "o"                               ;; where cohort docs are on disk (unique)
 :cohort/server-dir "o"                                ;; where cohort docs are on the server (unique)
 :cohort/repo-path  "/home/teodorlu/dev/iterate/olorm" ;; where the repo is
 }

;; a _doc_ is a written thing

{:doc/number 42       ;; document number, starts at 1
 :doc/slug "olorm-42" ;; the documents slug is used on the URL. Globally unique
 :doc/cohort :olorm   ;; refers to a cohort ident
 }
