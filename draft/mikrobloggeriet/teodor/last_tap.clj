{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(ns mikrobloggeriet.teodor.last-tap
  (:require
   [nextjournal.clerk :as clerk]))

;; ## Last tap

(defonce last-tapped (atom nil))

(comment
  (tap> (rand))
  @last-tapped

  )

(defn store-tapped [val]
  (reset! last-tapped val)
  (clerk/recompute!))

(add-tap #'store-tapped)

{::clerk/visibility {:code :show :result :show}}

^{::clerk/budget nil
  ::clerk/auto-expand-results? true}
@last-tapped

^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
