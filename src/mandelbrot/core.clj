(ns mandelbrot.core
  (:import (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)
           (java.awt Color)))

(defn complex-mult [re im]
  [(+' (*' re re) (-' (*' im im)))
   (*' 2 (*' re im))])

(defn mandelbrot-iter [z c]
  (apply vector (map +' (complex-mult (first z) (second z)) c)))

(defn abs [n] (max n (-' n)))

(defn greater-than [f z] (> (abs (f z)) 500))

(defn mandel-iters [zinit cinit]
  (loop [z zinit c (map float cinit) iters 0]
    (if (>= iters (- (* 256 1) 1))
      (- (* 256 1) 1)
      (if (or (greater-than first z) (greater-than second z))
        iters
        (recur (mandelbrot-iter z c) c (+ iters 1))))))

(defn gen-mandels [x_min x_max y_min y_max step]
  (for [x (range x_min (+ x_max step) step) y (range y_min (+ y_max step) step)]
    [x y (mandel-iters [0 0] [x y])]))

(defn adjust [i limit step block_size] (* (/ 1 step) (- i limit) block_size))

(defn plot-mandels [g x_min x_max y_min y_max step block_size]
  (doseq [[x y block] (gen-mandels x_min x_max y_min y_max step)]
    (.setColor g (Color. (* 16 (quot block 16)) (* 16 (mod block 16)) 0))
    (.fillRect g (adjust x x_min step block_size) (adjust y y_min step block_size) block_size block_size)))

(defn draw-mandels [filename x_min x_max y_min y_max step block_size]
  (defn image-size [c_min c_max step block_size] (* (/ (- c_max c_min) step) block_size))
  (def bi (BufferedImage.
            (image-size x_min x_max step block_size)
            (image-size y_min y_max step block_size)
            BufferedImage/TYPE_INT_ARGB))
  (def gfx (.createGraphics bi))
  (plot-mandels gfx x_min x_max y_min y_max step block_size)
  (ImageIO/write bi "png" (File. (str filename ".png")))
  )

(comment (draw-mandels "test2" -2 2 -9/8 9/8 1/480 1))
(comment (draw-mandels "test3" -4 4 -9/4 9/4 1/240 1))
