(ns game-of-life.cell
  (:require [clojure.core :as core]))

(defn get
  "get cell at position x y"
  [cells x y]
  (core/get (core/get cells y) x))

(defn set
  "sets cell state"
  ([cells x y] (core/update-in cells [y x] core/not))
  ([cells x y state] (core/assoc-in cells [y x] state)))
