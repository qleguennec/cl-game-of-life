(ns game-of-life.util
  (:require
   [game-of-life.cell :as cell]
   [game-of-life.config :refer [*cell-n-horiz* *cell-n-vert*]]))

(defn diff-cells
  "generates a diff list between gen-a and gen-b"
  [gen-a gen-b]
  (mapv
   (fn [x y]
     (second
      (reduce
       (fn [[j acc] [x y]]
         (conj [(+ 1 j)]
               (if (= x y)
                 acc
                 (conj acc j))))
       [0 []]
       (map #(conj (vector %1) %2) x y)))) gen-a gen-b))

(defn benchmark
  "benchmars functions f n times on random cells"
  [cells f n]
  (#(/ (reduce + 0 %) n)
   (for [x (range n)]
     (let [start (. System (nanoTime))
           _ (dorun (take n (iterate f cells)))]
       (/ (double (- (. System (nanoTime)) start)) 1000000.0)))))

(defn empty-cells
  "generates empty board"
  []
  (let [row (vec (repeat *cell-n-horiz* false))]
    (vec (repeat *cell-n-vert* row))))

(defn reduce-cells
  "maps function f over cell cooridnates"
  [cells f val]
  ((fn [x y acc]
    (let [p (= x (- *cell-n-horiz* 1))]
      (if (and p (= y (- *cell-n-vert* 1)))
        (f acc cells x y)
        (recur
          (if p 0 (+ x 1))
          (if p (+ y 1) y)
          (f acc cells x y))))) 0 0 val))

(defn filter-cells
  "filters with predicate p over cells coordinates"
  [cells p]
  (reduce-cells cells
                (fn [acc cells x y]
                  (if (p cells x y)
                    (conj acc [x y])
                    acc)) []))

(defn random-cell
  "selects a random dead cell and makes it alive
  could possibly run infinitely"
  [cells]
  (assoc-in cells
            ((fn [[a b]] [b a])
             (rand-nth
              (filter-cells
               cells
               (comp not cell/get)))) true))

(defn random-cells-pop
  "generates random cells based on strict population count"
  [n]
  (take n (iterate random-cell empty-cells)))

(defn random-cells-pop-density
  "generates random cells based on a fill ratio"
  [density]
  (random-cells-pop (* density (* *cell-n-vert* *cell-n-horiz*))))

(defn with-density
  "executes f on random grid w x h"
  [w h density f & args]
  (with-bindings {#'*cell-n-horiz* w #'*cell-n-vert* h}
    (apply f (cons (random-cells-pop-density density) args))))
