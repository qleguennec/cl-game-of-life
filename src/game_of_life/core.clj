(ns game-of-life.core
  (:require
    [game-of-life.ui :refer [init-window]]
    [game-of-life.gen :refer [random-cells]]))

(defn -main
  []
  (let [cells (atom (random-cells))]
    (init-window cells)))
