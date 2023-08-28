(ns johan.req)

{:remote-addr "0:0:0:0:0:0:0:1",
 :start-time 248603260634916,
 :params {:slug "oj-2"},
 :mikrobloggeriet.doc/slug "oj-2",
 :route-params {:slug "oj-2"},
 :headers
 {"sec-fetch-site" "none",
  "sec-ch-ua-mobile" "?0",
  "host" "localhost:7223",
  "user-agent"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
  "cookie" "flag=oj",
  "sec-fetch-user" "?1",
  "sec-ch-ua"
  "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome
\";v=\"116\"",
  "sec-ch-ua-platform" "\"macOS\"",
  "connection" "keep-alive",
  "upgrade-insecure-requests" "1",
  "accept"
  "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
  "accept-language" "en-GB,en-US;q=0.9,en;q=0.8",
  "sec-fetch-dest" "document",
  "accept-encoding" "gzip, deflate, br",
  "sec-fetch-mode" "navigate",
  "cache-control" "max-age=0"},
 :mikrobloggeriet/cohort
 {:cohort/id :oj,
  :cohort/members

  [{:author/first-name "Johan"} {:author/first-name "Olav"}],
  :cohort/root "text/oj"},
 :async-channel
 #object[org.httpkit.server.AsyncChannel 0x21d1cfe2 "/[0:0:0:0:0:0:0:1]:7223<->/[0:0:0:0:0:0:0:1]:64241"],
 :server-port 7223,
 :content-length 0,
 :compojure/route [:get "/oj/:slug"],
 :websocket? false,
 :content-type nil,
 :character-encoding "utf8",
 :uri "/oj/oj-2",
 :server-name "localhost",
 :query-string nil,
 :body nil,
 :scheme :http,
 :request-method :get}