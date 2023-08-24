(ns mikrobloggeriet.teodor.redis-cache)

;; Dette navnerommet krever at du _først_ starter opp redis på PC-en. Feks sånn:
;;
;;     $ brew install redis
;;     $ redis-server
;;     ....
;;     97958:M 23 Aug 2023 16:20:39.749 * Ready to accept connections tcp
;;
;; Jeg følger dokumentasjonen til Carmine, en redis-klient til Clojure:
;;
;; https://github.com/taoensso/carmine/wiki/1-Getting-started
