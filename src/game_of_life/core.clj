(ns game-of-life.core
  (:require
    [game-of-life.config :refer [*cell-n-horiz* *cell-n-vert*]]
    [game-of-life.ui :refer [init-window]]
    [game-of-life.gen :refer [random-cells-pop-ratio]]))

(defn -repl
  []
  (map refer '(game-of-life.gen
               game-of-life.ui
               game-of-life.util
               game-of-life.config
               game-of-life.cell)))

(defn -main
  []
  (let [cells (atom (random-cells-pop-ratio 0.5))]
    (init-window cells)))
