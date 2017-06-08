(ns game-of-life.core
  (:require [clojure.core.reducers :as r])
  (:import [java.awt Graphics Color Dimension]
           [java.awt.event KeyListener KeyEvent]
           [java.awt.image BufferedImage]
           [javax.swing JFrame JPanel SwingUtilities]))


(def globals
  {:win-name "Game of life"
   :win-width 1600
   :win-height 900
   :cell-width 10
   :cell-height 10
   :cell-fill-width 9
   :cell-fill-height 9
   :cells (atom nil)})

(defn get-cell
  [cells x y]
  (get (get cells y) x))

(defn get-n-cells
  []
  [(int (/ (:win-width globals) (:cell-fill-width globals) ))
   (int (/ (:win-height globals) (:cell-fill-height globals)))])

(defn get-next-cell-state
  [cells x y]
  (case (reduce
         #(if (get-cell cells (first %2) (second %2)) (+ %1 1) %1)
         0
         [[(- x 1) (- y 1)] [(- x 1) y] [(- x 1) (+ y 1)]
          [x (- y 1)] [x (+ y 1)]
          [(+ x 1) (- y 1)] [(+ x 1) y] [(+ x 1) (+ y 1)]])
    2 (get-cell cells x y)
    3 true
    false))

(defn gen-diff
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

(defn next-gen-1
  [cells]
  ((fn [x y acc row]
     (let [[w h] (get-n-cells)
           last (and (= w (+ x 1)) (= h (+ y 1)))
           next-x (if (= w (+ x 1)) 0 (+ x 1))
           next-y (if (= w (+ x 1)) (+ y 1) y)]
       (if last (conj acc row)
           (let [next-cell (conj row (get-next-cell-state cells x y))]
             (recur next-x next-y
                    (if (zero? next-x)
                      (conj acc next-cell)
                      acc)
                    (if (zero? next-x) [] next-cell)))))) 0 0 [] []))

(defn next-gen-2
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
          (let [new-state (get-next-cell-state cells j i)]
            (conj [(+ j 1)]
                  (if (not= cell new-state)
                    (conj coll j)
                    coll)))) [0 []] row)))
    (range (first (get-n-cells))) cells)))

(defn random-cells
  []
  (let [[w h] (get-n-cells)]
    (let [row (repeat w (partial rand-int))]
      (reduce (fn [coll _] (conj coll (mapv #(zero? (% 2)) row)))
              []
              (range h)))))

(defn keyboard
  [frame panel key]
  (let [cells (:cells globals)]
    (cond
      (= key \q) (.dispose frame)
      (= key \r) (reset! cells (random-cells))
      (= key \a) (reset! cells (next-gen-1 @cells))
      (= key \b) (reset! cells (next-gen-2 @cells)))
    )
  (.updateUI panel))

(defn render
  [^Graphics g]
  (let [cells @(:cells globals)
        [w h] (get-n-cells)]
    (doseq [x (range w)
            y (range h)]
      (doto g
        (.setColor
         (if (get-cell cells x y)
           Color/RED
           Color/BLACK))
        (.fillRect
         (* x (:cell-width globals))
         (* y (:cell-height globals))
         (:cell-fill-width globals)
         (:cell-fill-height globals))))))

(defn init-window
  []
  (let [^JFrame frame (JFrame. (:win-name globals))
        panel (proxy [JPanel] []
                (paint [^Graphics g]
                  (render g)))
        key-listener (proxy [KeyListener] []
                       (keyPressed [^KeyEvent e]
                         (keyboard frame panel
                                   (.getKeyChar e)))
                       (keyReleased [e])
                       (keyTyped [e]))]
    (doto frame
      (.add panel)
      (.setSize (:win-width globals) (:win-height globals))
      (.addKeyListener key-listener)
      (.setVisible true))))

(defn benchmark
  [f n]
  (#(/ (reduce + 0 %) n)
   (for [x (range n)]
     (let [start (. System (nanoTime))
           _ (dorun (take n (iterate f (random-cells))))]
       (/ (double (- (. System (nanoTime)) start)) 1000000.0)))))


(defn -main
  []
  (reset! (:cells globals) (random-cells))
  (init-window))
