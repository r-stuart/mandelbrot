# Mandelbrot Generator

A Clojure generator for [Mandelbrot sets](https://en.wikipedia.org/wiki/Mandelbrot_set).

## Usage

Chain together the commands `gen-coords`, `gen-mandels`, `plot-mandels` and `write-to-file` to create the desired Mandelbrot set image.

```
(-> (gen-coords -2 2 -9/8 9/8 1/480)
    (gen-mandels 256 500 8)
    (plot-mandels 1 colours/summer)
    (write-to-file "docs/example"))
```

This example will produce the image `docs/example.png`.

![Example Mandelbrot](docs/example.png?raw=true)

To play around with different graphics in a live window:

```
;; Generate the data points at the appropriate resolution
(def example-plot
  (doall
    (-> (gen-coords -2 2 -9/8 9/8 1/480)
        (gen-mandels 256 500 8))))

;; Create a frame
(def frame (Frame.))

;; Sample different colours without regenerating the data
(-> example-plot
    (plot-mandels 1 colours/wiki)
    (draw-to-screen frame))
```

## License

Copyright © 2020 Ryan Stuart

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
