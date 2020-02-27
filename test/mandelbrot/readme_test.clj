(ns mandelbrot.readme-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]))

(deftest readme-exists
  (testing "Readme file is present"
    (is (.exists (io/file "README.md")))))

(deftest examples-run
  (testing "Readme examples successfully run"
    (let [example-image "docs/example.png"]
      (io/delete-file example-image)
      (let [examples [(take-nth 2 (rest (clojure.string/split (slurp "README.md") #"```")))]]
        (map eval (map read-string examples)))
      (is (.exists (io/file example-image))))
    ))