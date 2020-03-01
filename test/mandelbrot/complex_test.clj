(ns mandelbrot.complex-test
  (:require [clojure.test :refer :all]
            [mandelbrot.complex :refer :all]))

(deftest add-test
  (testing "Test adding values."
    (let [z1 (->Complex 3 5)
          z2 (->Complex 4 1)
          result (->Complex 7 6)]
      (is (= (add z1 z2) result)))))

(deftest mult-test
  (testing "Test multiplying values."
    (let [z1 (->Complex 3 5)
          z2 (->Complex 4 1)
          result (->Complex 7 23)]
      (is (= (multiply z1 z2) result)))))