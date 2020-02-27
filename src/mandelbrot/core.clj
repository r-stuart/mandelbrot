(ns mandelbrot.core
  (:import (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)
           (java.awt Color Frame Dimension))
  (:require [mandelbrot.maths :as maths]
            [mandelbrot.colours :as colours]))

(defn- mandel-iters [z-init c-init max-iters escape]
  (let [n (dec max-iters)
        escaped? (fn [z] (some? (some identity (map #(> (maths/abs (get z %)) escape) [0 1]))))
        mandel-step (fn [z c] (apply vector (map +' (maths/complex-square (first z) (second z)) c)))]
    (loop [z z-init c (map float c-init) i 0]
      (if (>= i n)
        n
        (if (escaped? z)
          i
          (recur (mandel-step z c) c (inc i)))))))

(defn gen-coords [x-min x-max y-min y-max step]
  (for [x (range x-min (+ x-max step) step) y (range y-min (+ y-max step) step)] [x y]))

(defn- parallel-buckets [f n coll]
  (let [split-coll (partition-all n coll)]
    (apply concat (pmap #(doall (map f %)) split-coll))))

(defn- iter-function [xy n escape] (let [x (first xy)
                                                y (second xy)]
                                            [x y (mandel-iters [0 0] [x y] n escape)]))

(defn gen-mandels
  ([xys n escape] (map iter-function xys n escape))
  ([xys n escape buckets] (parallel-buckets #(iter-function % n escape) buckets xys)))

(defn plot-mandels [points block-size colour-func]
  (let [extract-range (fn [ps index] (let [index-points (map #(get % index) ps)]
                                       [(apply min index-points) (apply max index-points)]))
        extract-step (fn [ps] (let [index-points (set (map #(get % 0) ps))]
                                (/ (- (apply max index-points) (apply min index-points)) (- (count index-points) 1))))
        x-range (extract-range points 0)
        y-range (extract-range points 1)
        step (extract-step points)
        adjust (fn [i limit block-size] (* (/ 1 step) (- i limit) block-size))
        image-size (fn [c-min c-max step block-size] (* (/ (- c-max c-min) step) block-size))
        bi (BufferedImage.
             (image-size (first x-range) (second x-range) step block-size)
             (image-size (first y-range) (second y-range) step block-size)
             BufferedImage/TYPE_INT_ARGB)
        gfx (.createGraphics bi)]
    (doseq [[x y block] points]
      (let [[r g b] (colour-func block)]
        (.setColor gfx (Color. r g b)))
      (.fillRect gfx
                 (adjust x (first x-range) block-size)
                 (adjust y (first y-range) block-size)
                 block-size
                 block-size))
    bi
    ))

(defn write-to-file [plot filename]
  (io! (ImageIO/write plot "png" (File. (str filename ".png")))))

(defn draw-to-screen [plot frame]
  (.setVisible frame true)
  (comment (.setSize frame (Dimension. 512 512)))
  (let [gfx (.getGraphics frame)]
    (io! (.drawImage gfx plot 0 0 nil))))
