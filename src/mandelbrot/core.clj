(ns mandelbrot.core
  (:import (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)
           (java.awt Color))
  (:require [mandelbrot.maths :as maths]))

(defn mandelbrot-iter [z c]
  (apply vector (map +' (maths/complex-square (first z) (second z)) c)))

(defn mandel-iters [z_init c_init max_iters]
  (let [end_cond (- max_iters 1)]
    (loop [z z_init c (map float c_init) iters 0]
      (if (>= iters end_cond)
        end_cond
        (if (not (nil? (some identity (map #(> (maths/abs (get z %)) 500) [0 1]))))
          iters
          (recur (mandelbrot-iter z c) c (+ iters 1)))))))

(defn gen-mandels [x_min x_max y_min y_max step]
  (for [x (range x_min (+ x_max step) step) y (range y_min (+ y_max step) step)]
    [x y (mandel-iters [0 0] [x y] 256)]))

(defn extract-range [points index]
  (let [index_points (map #(get % index) points)]
    [(apply min index_points) (apply max index_points)]))

(defn extract-step [points]
  (let [index_points (set (map #(get % 0) points))]
    (/ (- (apply max index_points) (apply min index_points)) (- (count index_points) 1))))

(defn plot-mandels [g points block_size]
  (let [x-range (extract-range points 0)
        y-range (extract-range points 1)
        step (extract-step points)]
    (defn adjust [i limit step block_size] (* (/ 1 step) (- i limit) block_size))
    (doseq [[x y block] points]
      (.setColor g (Color. (* 16 (quot block 16)) (* 16 (mod block 16)) 0))
      (.fillRect g
                 (adjust x (first x-range) step block_size)
                 (adjust y (first y-range) step block_size)
                 block_size
                 block_size))
    ))

(defn draw-mandels [filename points block_size]
  (let [step (extract-step points)
        x_range (extract-range points 0)
        y_range (extract-range points 1)]
    (defn image-size [c_min c_max step block_size] (* (/ (- c_max c_min) step) block_size))
    (def bi (BufferedImage.
              (image-size (first x_range) (second x_range) step block_size)
              (image-size (first y_range) (second y_range) step block_size)
              BufferedImage/TYPE_INT_ARGB))
    (def gfx (.createGraphics bi))
    (plot-mandels gfx points block_size)
    (ImageIO/write bi "png" (File. (str filename ".png")))
    )
  )

(comment (draw-mandels "test" (gen-mandels -2 2 -2 2 1/32) 1))

(comment (draw-mandels "test2" (gen-mandels -2 2 -9/8 9/8 1/480) 1))
(comment (draw-mandels "test3" (gen-mandels -4 4 -9/4 9/4 1/240) 1))
