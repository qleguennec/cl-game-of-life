(ns game-of-life.gen
  (:require [game-of-life.config :refer [*cell-n-horiz* *cell-n-vert*]]))


(defn next-cell-state
  "get state of the cell at position x y on the next generation"
  [cells x y]
  (let [neighbours
          '([(- x 1) (- y 1)] [(- x 1) y] [(- x 1) (+ y 1)]
            [x (- y 1)] [x (+ y 1)]
            [(+ x 1) (- y 1)] [(+ x 1) y] [(+ x 1) (+ y 1)])]
    ((fn [n [c & rest]]
        (cond
          (> 3 n) false
          (empty? rest) (if (= n 2) (cell/get x y) true)
          :else (recur
                  (if (cell/get cells (first c) (second c))
                    (+ n 1)
                    n)
                  rest)))
     0 neighbours)))

(defn next-1
  "first generator algorithm - rather slow"
  [cells]
  ((fn [x y acc row]
     (let [last (and
                 (= *cell-n-horiz* (+ x 1))
                 (= *cell-n-vert* (+ y 1)))
           next-x (if (= *cell-n-horiz* (+ x 1)) 0 (+ x 1))
           next-y (if (= *cell-n-horiz* (+ x 1)) (+ y 1) y)]
       (if last (conj acc row)
           (let [next-cell (conj row (next-cell-state cells x y))]
             (recur next-x next-y
                    (if (zero? next-x)
                      (conj acc next-cell)
                      acc)
                    (if (zero? next-x) [] next-cell)))))) 0 0 [] []))

(defn next-2
  "faster, concurrent"
  [cells]
  (mapv
   (fn [c i]
     (reduce #(update-in %1 [%2] not) c i))
   cells
   (pmap
    (fn [i row]
      (second
       (reduce
        (fn [[j coll] cell]
          (let [new-state (next-cell-state cells j i)]
            (conj [(+ j 1)]
                  (if (not= cell new-state)
                    (conj coll j)
                    coll)))) [0 []] row)))
    (range *cell-n-horiz*) cells)))
