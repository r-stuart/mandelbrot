(ns mandelbrot.complex)


(defrecord Complex [re im])

(defn add
  [^Complex z1 ^Complex z2] (->Complex (+ (.re z1) (.re z2)) (+ (.im z1) (.im z2))))

(defn multiply [^Complex z1 ^Complex z2]
  (let [x1 (.re z1)
        x2 (.re z2)
        y1 (.im z1)
        y2 (.im z2)]
    (->Complex (- (* x1 x2) (* y1 y2)) (+ (* x1 y2) (* x2 y1)))))