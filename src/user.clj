(ns user
  (:require [clojure.string :as str]
            [babashka.fs :as fs]))

;; Convenience functions when you start a REPL. The default user namespace is
;; always 'user. I'm putting functions here to make it easy to start the server
;; and start doing stuff.
;;
;; Example usage:
;;
;;     $ pwd
;;     /home/teodorlu/dev/iterate/mikrobloggeriet/serve
;;     $ clj
;;     Clojure 1.11.1
;;     user=> (start!)
;;     mikrobloggeriet.serve running: http://localhost:7223
;;     #object[clojure.lang.AFunction$1 0x125a8ab6 "clojure.lang.AFunction$1@125a8ab6"]
;;
;; Though starting a REPL from your editor (Calva for VScode, Cursive for
;; IntelliJ) is strongly recommended over using starting the REPL from a
;; terminal.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; I avoid using :require in the 'user namespace.
;;
;; Because then the REPL always start, even if there's error other places in my
;; code! `require-resolve` is a dynamic require, and will crash runtime rather
;; than compile-time if there are import errors.

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start!
  ([]
   (start! {}))
  ([opts]
   (let [opts (merge {:browse? true} opts)
         start-fn (requiring-resolve 'mikrobloggeriet.serve/start!)
         port (requiring-resolve 'mikrobloggeriet.serve/port)
         shell (requiring-resolve 'babashka.process/shell)]
     (start-fn {})
     (let [browser (System/getenv "BROWSER")
           url (str "http://localhost:" (deref port))]
       (when (:browse? opts)
         (cond (not (str/blank? browser)) (do (shell browser url) nil)
               (fs/which "open") (shell "open" url)
               :else (println "Please open" url "in your web browser.")))))))

(defn require-application-code!
  "loads most/all of the Mikrobloggeriet code into memory

  Nice to use if you want to do some kind of dynamic analysis, like finding
  deprecated vars. Then you need to have the application code loaded. That
  doesn't happen with a clean REPL."
  []
  (require 'mikrobloggeriet.serve)
  (require 'mikrobloggeriet.cli)
  (require 'mikrobloggeriet.olorm-cli)
  (require 'mikrobloggeriet.jals-cli))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop! []
  (let [stop-fn (requiring-resolve 'mikrobloggeriet.serve/stop!)]
    (stop-fn)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clerk-start!
  ([] (clerk-start! {}))
  ([opts]
   (let [clerk-serve (requiring-resolve 'nextjournal.clerk/serve!)
         clerk-port 7743
         opts (merge {:browse? true :port clerk-port} opts)]
     (clerk-serve opts))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clerk-tap-inspector! []
  (let [show (requiring-resolve 'nextjournal.clerk/show!)]
    (show 'nextjournal.clerk.tap)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn teodor-start! []
  (start!)
  (clerk-start!))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn teodor-start-silent! []
  (let [silent {:browse? false}]
    (start! silent)
    (clerk-start! silent)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clerk-start-watch! []
  (let [clerk-serve (requiring-resolve 'nextjournal.clerk/serve!)
        clerk-port 7743]
    (clerk-serve {:browse? true
                  :port clerk-port
                  :watch-paths ["src" "test" "draft"]})))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clerk-stop! []
  (let [clerk-halt (requiring-resolve 'nextjournal.clerk/halt!)]
    (clerk-halt)))
