(ns mandelbrot.readme-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]))

(deftest readme-exists
  (testing "Readme file is present"
    (is (.exists (io/file "README.md")))))

(comment (deftest examples-run
  (testing "Readme examples successfully run"
    (let [docs (io/file "docs")
          example-image "docs/example.png"]
      (if-not (.exists docs) (.mkdir docs))
      (if (.exists (io/file example-image)) (io/delete-file example-image))
      (require '[mandelbrot.core :refer :all])
      (require '[mandelbrot.colours :as colours])
      (doall (map eval (map read-string (take-nth 2 (rest (clojure.string/split (slurp "README.md") #"```"))))))
      (is (.exists (io/file example-image))))
    )))