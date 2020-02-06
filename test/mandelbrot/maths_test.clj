(ns mandelbrot.maths-test
  (:require [clojure.test :refer :all]
            [mandelbrot.maths :refer :all]))

(deftest abs-test
  (testing "Test absolute values."
    (is (=
          (abs -2)
          2))))

(deftest mult-test
  (testing "Test complex square."
    (is (=
          (complex-square 4 -7)
          [(+ (* 4 4) (- (* -7 -7))) (* 2 (* 4 -7))]
          ))))
