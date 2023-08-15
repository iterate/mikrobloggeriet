(ns johan.draw)

( def pool "olr")

(rand-nth pool)

(def mapping (hash-map \o "Oddmund" \l "Lars" \r "Richard"))
(defn draw 
  "Should draw random from a pool of char"
  [pool]
  (get mapping (rand-nth pool))
  )

(draw pool)
( def req
  {:remote-addr "0:0:0:0:0:0:0:1",
   :start-time 54521377543833,
   :params {:pool "o"},
   :route-params {:pool "o"},
   :headers
   {"sec-fetch-site" "none",
    "sec-ch-ua-mobile" "?0",
    "host" "localhost:7223",
    "user-agent"
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36",
    "sec-fetch-user" "?1",
    "sec-ch-ua"
    "\"Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"115\", \"Chromium\";v=\"115\"",
    "sec-ch-ua-platform" "\"macOS\"",
    "connection
" "keep-alive",
    "upgrade-insecure-requests" "1",
    "accept"
    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
    "accept-language" "en-GB,en-US;q=0.9,en;q=0.8",
    "sec-fetch-dest" "document",
    "accept-encoding" "gzip, deflate, br",
    "sec-fetch-mode" "navigate",
    "cache-control" "max-age=0"} 
   :server-port 7223,
   :content-length 0,
   :compojure/route [:get "/olorm/draw/:pool"],
   :websocket? false,
   :content-type nil,
   :character-encoding "utf8",
   :uri "/olorm/draw/o",
   :server-name "localhost",
   :query-string nil,
   :body nil,
   :scheme :http,
   :request-method :get})

(get-in req [ :params :pool])

(defn get-pool
  "get pool from req"
  [req]
  (get req :params))