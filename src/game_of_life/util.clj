(ns game-of-life.util)

(defn cell-diff
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
  [f n]
  (#(/ (reduce + 0 %) n)
   (for [x (range n)]
     (let [start (. System (nanoTime))
           _ (dorun (take n (iterate f (random-cells))))]
       (/ (double (- (. System (nanoTime)) start)) 1000000.0)))))
