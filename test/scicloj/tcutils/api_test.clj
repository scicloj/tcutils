(ns scicloj.tcutils.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [tablecloth.api :as tc]
            [scicloj.tcutils.api :as sut]))

(deftest lag-test
  (testing "with valid input"
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

    (testing "fills in `nil` for lagging non-numeric values"
      (is (= {:C1 ["a" "b" "c" "d"]
              :lag-1 [nil "a" "b" "c"]}
             (-> (tc/dataset {:C1 ["a" "b" "c" "d"]})
                 (sut/lag :lag-1 :C1 1)
                 (tc/columns :as-map)))))))
