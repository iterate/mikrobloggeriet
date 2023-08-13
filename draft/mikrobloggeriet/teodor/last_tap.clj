{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(ns mikrobloggeriet.teodor.last-tap
  (:require
   [nextjournal.clerk :as clerk]))

;; ## Last tap

(defonce previous-tap (atom nil))
(defonce latest-tap (atom nil))

(comment
  (tap> (rand))
  @latest-tap

  )


(defn store-tapped [val]
  (when @latest-tap
    (reset! previous-tap @latest-tap))
  (reset! latest-tap val)
  (clerk/recompute!))

(add-tap #'store-tapped)

{::clerk/visibility {:code :show :result :show}}

^{::clerk/budget nil
  ::clerk/auto-expand-results? true}
@latest-tap

^{::clerk/budget nil
  ::clerk/auto-expand-results? true}
@previous-tap


^
{:nextjournal.clerk/visibility {:code :hide}}
(clerk/html [:div {:style {:height "50vh"}}])
