(ns mandelbrot.core
  (:import (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)
           (java.awt Color))
  (:require [mandelbrot.maths :as maths]))

(defn mandel-iters [z_init c_init max_iters escape]
  (let [end_cond (- max_iters 1)
        escaped? (fn [z] (not (nil? (some identity (map #(> (maths/abs (get z %)) escape) [0 1])))))
        mandel-step (fn [z c] (apply vector (map +' (maths/complex-square (first z) (second z)) c)))]
    (loop [z z_init c (map float c_init) iters 0]
      (if (>= iters end_cond)
        end_cond
        (if (escaped? z)
          iters
          (recur (mandel-step z c) c (+ iters 1)))))))

(defn gen-mandels [x_min x_max y_min y_max step max_iters escape]
  (for [x (range x_min (+ x_max step) step) y (range y_min (+ y_max step) step)]
    [x y (mandel-iters [0 0] [x y] max_iters escape)]))

(defn plot-mandels [points block_size]
  (let [extract-range (fn [ps index] (let [index_points (map #(get % index) ps)]
                                       [(apply min index_points) (apply max index_points)]))
        extract-step (fn [ps]   (let [index_points (set (map #(get % 0) ps))]
                                  (/ (- (apply max index_points) (apply min index_points)) (- (count index_points) 1))))
        x_range (extract-range points 0)
        y_range (extract-range points 1)
        adjust (fn [i limit step block_size] (* (/ 1 step) (- i limit) block_size))
        image-size (fn [c_min c_max step block_size] (* (/ (- c_max c_min) step) block_size))
        step (extract-step points)
        bi (BufferedImage.
             (image-size (first x_range) (second x_range) step block_size)
             (image-size (first y_range) (second y_range) step block_size)
             BufferedImage/TYPE_INT_ARGB)
        gfx (.createGraphics bi)]
    (doseq [[x y block] points]
      (.setColor gfx (Color. (* 16 (quot block 16)) (* 16 (mod block 16)) 0))
      (.fillRect gfx
                 (adjust x (first x_range) step block_size)
                 (adjust y (first y_range) step block_size)
                 block_size
                 block_size))
    bi
    ))

(defn draw-mandels [filename points block_size]
  (ImageIO/write (plot-mandels points block_size) "png" (File. (str filename ".png"))))

(comment (draw-mandels "test" (gen-mandels -2 2 -2 2 1/32 256 500) 1))

(comment (draw-mandels "test2" (gen-mandels -2 2 -9/8 9/8 1/480 256 500) 1))
(comment (draw-mandels "test3" (gen-mandels -4 4 -9/4 9/4 1/240 256 500) 1))
