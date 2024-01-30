(ns mikrobloggeriet.teodor.routing
  (:require
   [reitit.core :as reitit]))

(let [router (reitit/router [["/offers/" {:get (fn [_req] "our offers!")}]])]
  (reitit/match-by-path router "/offers/"))
;; => {:template "/offers/",
;;     :data {:get #function[mikrobloggeriet.teodor.routing/eval30688/fn--30689]},
;;     :result nil,
;;     :path-params {},
;;     :path "/offers/"}
