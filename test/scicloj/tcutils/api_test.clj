(ns scicloj.tcutils.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.tcutils.api :as sut]))

(deftest lag-test
  (testing "with valid arguments"
    (testing "fills in 0 for lagging numeric values"
      (is (= {:C1 [1 2 3 4 5]
              :lag-1 [0 1 2 3 4]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/lag :lag-1 :C1 1)
                 (tc/columns :as-map)))))

    (testing "lags by given number of rows"
      (is (= {:C1 [1 2 3 4 5]
              :lag-1 [0 0 0 1 2]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/lag :lag-1 :C1 3)
                 (tc/columns :as-map)))))

    (testing "generates new column name if not given one"
      (is (= {:C1 [1 2 3 4 5]
              :C1-lag-2 [0 0 1 2 3]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/lag :C1 2)
                 (tc/columns :as-map)))))

    (testing "fills in `nil` for lagging non-numeric values"
      (is (= {:C1 ["a" "b" "c" "d"]
              :lag-1 [nil "a" "b" "c"]}
             (-> (tc/dataset {:C1 ["a" "b" "c" "d"]})
                 (sut/lag :lag-1 :C1 1)
                 (tc/columns :as-map)))))))

(deftest lead-test
  (testing "with valid-input"
    (testing "fills in 0 for leading numeric values"
      (is (= {:C1 [1 2 3 4 5]
              :lead-1 [2 3 4 5 0]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/lead :lead-1 :C1 1)
                 (tc/columns :as-map)))))

    (testing "generates new column name if not given one"
      (is (= {:C1 [1 2 3 4 5]
              :C1-lead-2 [3 4 5 0 0]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/lead :C1 2)
                 (tc/columns :as-map)))))

    (testing "fills in `nil` for leading non-numeric values"
      (is (= {:C1 ["a" "b" "c" "d" "e"]
              :lead-3 ["d" "e" nil nil nil]}
             (-> (tc/dataset {:C1 ["a" "b" "c" "d" "e"]})
                 (sut/lead :lead-3 :C1 3)
                 (tc/columns :as-map)))))))

(deftest cumsum-test
  (testing "with valid inputs"
    (testing "calculates the cumulative sum of a column in a new named column"
      (is (= {:C1 [1 2 3 4 5]
              :sum [1.0 3.0 6.0 10.0 15.0]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/cumsum :sum :C1)
                 (tc/columns :as-map)))))

    (testing "generates new column name if not given one"
      (is (= {:C1 [1 2 3 4 5]
              :C1-cumulative-sum [1.0 3.0 6.0 10.0 15.0]}
             (-> (tc/dataset {:C1 [1 2 3 4 5]})
                 (sut/cumsum :C1)
                 (tc/columns :as-map)))))))

(deftest clean-column-names
  (testing "normalizes column names to snake-cased keywords"
    ;; See `clean-string` test for in-depth examples


    ))
