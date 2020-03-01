(ns mandelbrot.core
  (:import (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)
           (java.awt Color Frame Dimension))
  (:require [mandelbrot.colours :as colours]
            [mandelbrot.complex :as complex]))

(defn- mandel-iters [z-init c-init max-iters escape]
  (let [n (dec max-iters)
        escaped? (fn [z] (or (> (Math/abs (:re z)) escape) (> (Math/abs (:im z)) escape)))
        mandel-step (fn [z c] (complex/add (complex/multiply z z) c))
        c-init-float (complex/->Complex (double (:re c-init)) (double (:im c-init)))]
    (loop [z z-init c c-init-float i 0]
      (if (>= i n)
        n
        (if (escaped? z)
          i
          (recur (mandel-step z c) c (inc i)))))))

(defn gen-coords [x-min x-max y-min y-max step]
  (for [x (range x-min (+ x-max step) step) y (range y-min (+ y-max step) step)] (complex/->Complex x y)))

(defn- parallel-buckets [f n coll]
  (let [split-coll (partition-all n coll)]
    (apply concat (pmap #(doall (map f %)) split-coll))))

(defn- iter-function [z n escape] [z (mandel-iters (complex/->Complex 0 0) z n escape)])

(defn gen-mandels
  ([zs n escape] (map #(iter-function % n escape) zs))
  ([zs n escape buckets] (parallel-buckets #(iter-function % n escape) buckets zs)))

(defn plot-mandels [points block-size colour-func]
  (let [co-ordinates (map #(nth % 0) points)
        extract-step (fn [ps] (let [index-points (set (map :re ps))]
                                (/ (- (apply max index-points) (apply min index-points)) (- (count index-points) 1))))
        x-range (map :re co-ordinates)
        y-range (map :im co-ordinates)
        step (extract-step co-ordinates)
        adjust (fn [i limit block-size] (* (/ 1 step) (- i limit) block-size))
        image-size (fn [c-min c-max step block-size] (* (/ (- c-max c-min) step) block-size))
        bi (BufferedImage.
             (image-size (first x-range) (last x-range) step block-size)
             (image-size (first y-range) (last y-range) step block-size)
             BufferedImage/TYPE_INT_ARGB)
        gfx (.createGraphics bi)]
    (doseq [[z block] points]
      (let [[r g b] (colour-func block)]
        (.setColor gfx (Color. r g b)))
      (.fillRect gfx
                 (adjust (:re z) (first x-range) block-size)
                 (adjust (:im z) (first y-range) block-size)
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
