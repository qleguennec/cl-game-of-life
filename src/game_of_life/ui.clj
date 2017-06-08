(ns game-of-life.ui
  (:require [game-of-life.gen
             :refer [next-1 next-2 get-cell random-cells]])
  (:import [java.awt Graphics Color Dimension]
          [java.awt.event KeyListener KeyEvent]
          [java.awt.image BufferedImage]
          [javax.swing JFrame JPanel SwingUtilities]))

(def ^:dynamic *win-name* "Game of life")
(def ^:dynamic *win-width* 1600)
(def ^:dynamic *win-height* 900)
(def ^:dynamic *cell-width* 10)
(def ^:dynamic *cell-height* 10)
(def ^:dynamic *cell-fill-width* 9)
(def ^:dynamic *cell-fill-height* 9)
(def ^:dynamic *cell-n-horiz* (/ *win-width* *cell-width*))
(def ^:dynamic *cell-n-vert* (/ *win-height* *cell-height*))

(defn keyboard
  "manages keyboard input"
  [frame cells panel key]
  (cond
    (= key \q) (.dispose frame)
    (= key \r) (reset! cells (random-cells))
    (= key \a) (reset! cells (next-1 @cells))
    (= key \b) (reset! cells (next-2 @cells)))
  (.updateUI panel))

(defn render
  [^Graphics g
   cells]
  (doseq [x (range *cell-n-horiz*)
          y (range *cell-n-vert*)]
    (doto g
      (.setColor
       (if (get-cell @cells x y)
         Color/RED
         Color/BLACK))
      (.fillRect
       (* x *cell-width*) (* y *cell-height*)
       *cell-fill-width* *cell-fill-height*))))

(defn init-window
  [cells]
  (let [^JFrame frame (JFrame. *win-name*)
        panel (proxy [JPanel] []
                (paint [^Graphics g]
                  (render g cells)))
        key-listener (proxy [KeyListener] []
                       (keyPressed [^KeyEvent e]
                         (keyboard frame cells panel
                                   (.getKeyChar e)))
                       (keyReleased [e])
                       (keyTyped [e]))]
    (doto frame
      (.add panel)
      (.setSize (+ *cell-fill-width* *win-width*)
                (+ *cell-fill-height* *win-height*))
      (.addKeyListener key-listener)
      (.setVisible true))))
