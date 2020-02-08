(ns mandelbrot.maths)

(defn abs [n]
  (max n (-' n)))

(defn complex-square [re im]
  [(+' (*' re re) (-' (*' im im)))
   (*' 2 (*' re im))])