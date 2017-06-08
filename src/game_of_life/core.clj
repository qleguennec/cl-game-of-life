(ns game-of-life.core
  (:require [game-of-life.ui :refer
             [init-window]]))

(defn -main
  []
  (let [cells (atom (random-cells))]
    (init-window cells)))
