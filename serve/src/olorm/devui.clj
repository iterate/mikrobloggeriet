(ns olorm.devui
  (:require [nextjournal.clerk :as clerk]))

(defonce xx (atom nil))

@xx

(clerk/table
 (for [[h v] (:headers @xx)]
   {:header h :value v}))

123123123
