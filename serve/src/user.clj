(ns user)

;; Convenience functions when you start a REPL. The default user namespace is
;; always 'user. I'm putting functions here to make it easy to start the server
;; and start doing stuff.
;;
;; Example usage:
;;
;;     $ pwd
;;     /home/teodorlu/dev/iterate/olorm/serve
;;     $ clj
;;     Clojure 1.11.1
;;     user=> (start!)
;;     olorm.serve running: http://localhost:7223
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
(defn start! []
  (let [start-fn (requiring-resolve 'olorm.serve/start!)]
    (start-fn {})))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop! []
  (let [stop-fn (requiring-resolve 'olorm.serve/stop!)]
    (stop-fn)))
