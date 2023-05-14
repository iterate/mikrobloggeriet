;; # Mikrobloggeriet
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
  (->html client
          {:slug "olorm-42"}))

;; ## TODO write the code

(defn ->client [opts]
  opts)
(defn ->html [client doc])
