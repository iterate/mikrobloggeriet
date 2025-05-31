(ns mblog.scenes
  (:require
   [mblog.ui :as ui]
   [portfolio.replicant :refer [defscene]]
   [portfolio.ui :as portfolio]))

(defscene example
  (ui/example))

(defn main []
  (portfolio/start! {}))
