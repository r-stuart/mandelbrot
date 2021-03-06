(ns mandelbrot.colours)

(defn summer [iters]
  [(- 255 (* 8 (quot iters 16)))
   (- 255 (* 16 (mod iters 16)))
   (- 127 (* 8 (quot iters 16)))])

(defn wiki [iters] (let [presets [[66 30 15]
                                  [25 7 26]
                                  [9 1 47]
                                  [4 4 73]
                                  [0 7 100]
                                  [12 44 138]
                                  [24 82 177]
                                  [57 125 209]
                                  [134 181 229]
                                  [211 236 248]
                                  [241 233 191]
                                  [248 201 95]
                                  [255 170 0]
                                  [204 128 0]
                                  [153 87 0]
                                  [106 52 3]]
                         black [0 0 0]]
                     (if (< 0 iters 255) (presets (mod iters 16)) black)
                     ))