{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(ns mikrobloggeriet.teodor.last-tap
  (:require
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer]))

;; ## Last tap

(defonce previous-tap (atom nil))
(defonce latest-tap (atom nil))

(comment
  (tap> (rand))
  @latest-tap)

(defn store-tapped [val]
  (when @latest-tap
    (reset! previous-tap @latest-tap))
  (reset! latest-tap val)
  (clerk/recompute!))

(add-tap #'store-tapped)

{::clerk/visibility {:code :show :result :show}}

nextjournal.clerk.viewer/map-viewer

^{::clerk/budget nil ::clerk/auto-expand-results? true}
(clerk/with-viewer (update nextjournal.clerk.viewer/map-viewer
                           :page-size (partial * 10))
  @latest-tap)

^{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  ^{::clerk/budget nil ::clerk/auto-expand-results? true}
  @previous-tap)
