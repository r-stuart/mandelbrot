(ns mandelbrot.readme-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [mandelbrot.core :refer :all]
            [mandelbrot.colours :as colours]))

(deftest readme-exists
  (testing "Readme file is present"
    (is (.exists (io/file "README.md")))))

(deftest examples-run
  (testing "Readme examples successfully run"
    (let [example-image "docs/example.png"]
      (io/delete-file example-image)
      (doall (map eval (map read-string (take-nth 2 (rest (clojure.string/split (slurp "README.md") #"```"))))))
      (is (.exists (io/file example-image))))
    ))