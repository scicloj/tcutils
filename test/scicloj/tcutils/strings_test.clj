(ns scicloj.tcutils.strings-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [clojure.test.check :as tch]
   [clojure.test.check.generators :as gen]
   [clojure.test.check.properties :as prop]
   [scicloj.tcutils.strings :as sut]))

(def stringable
  (prop/for-all [v gen/any]
                (try
                  (let [result (sut/to-clean-keyword v)]
                    (and (keyword? result)
                         (= 0 (count (re-seq #"[\p{P}&&[^-]]|[^\p{ASCII}]" (name result))))))
                  (catch Throwable ex
                    (if (str/includes? (ex-message ex) "Calling `to-clean-keyword`")
                      :pass
                      (throw ex))))))

;; Use test check to see if I missed any possibilities
(tch/quick-check 10000 stringable)

;; Still test some explicitly to make sure outputs are reasonable and document expected results
(deftest to-clean-keyword
  (testing "handles non-string inputs"
    (is (= :1 (sut/to-clean-keyword 1)))
    (is (= :1-0 (sut/to-clean-keyword 1.0)))

    (is (= :a-1-b-2 (sut/to-clean-keyword {:a 1 :b 2})))
    (is (= :a-1-b-2-3 (sut/to-clean-keyword {:a 1 :b [2 3]})))
    (is (= :1-2-3 (sut/to-clean-keyword [1 2 3])))

    (is (= :set-1-3-2 (sut/to-clean-keyword #{1 2 3})))
    (is (= :set-a-b-c (sut/to-clean-keyword #{"a" "b" "c"})))

    (is (= :keyword (sut/to-clean-keyword :keyword)))
    (is (= :keyword (sut/to-clean-keyword :keyword)))
    (is (= :keyword--test (sut/to-clean-keyword :keyword--test)))
    (is (= :keyword-test (sut/to-clean-keyword :KeywordTest))))

  (testing "removes punctuation"
    (is (= :test123 (sut/to-clean-keyword "test'123")))
    (is (= :test123 (sut/to-clean-keyword "test\"123")))
    (is (= :test123 (sut/to-clean-keyword "test/123")))
    (is (= :test-percent-123 (sut/to-clean-keyword "test%123")))
    (is (= :test-and-123 (sut/to-clean-keyword "test&123")))
    (is (= :test-number-123 (sut/to-clean-keyword "test#123")))
    (is (= :test-123 (sut/to-clean-keyword "test 123")))
    (is (= :test-123 (sut/to-clean-keyword "test_123")))
    (is (= :test-123 (sut/to-clean-keyword ".test.123")))
    (is (= :test-123 (sut/to-clean-keyword " test 123")))
    (is (= :test-123 (sut/to-clean-keyword "test.123.")))
    (is (= :test--123 (sut/to-clean-keyword "test..123")))
    (is (= :test-123 (sut/to-clean-keyword "test-123-")))
    (is (= :test-123 (sut/to-clean-keyword "test-123--")))
    (is (= :test-123 (sut/to-clean-keyword "test_123__")))
    (is (= :test-123 (sut/to-clean-keyword "test_123_"))))

  (testing "removes non-ascii characters"
    (is (= :studio-cafe (sut/to-clean-keyword "Studio Café")))
    (is (= :company (sut/to-clean-keyword "Company ©")))
    (is (= :gbp (sut/to-clean-keyword "£ (GBP)")))
    (is (= :s (sut/to-clean-keyword "µs"))))

  (testing "kebab-cases and keywordizes strings"
    (is (= :testing--this (sut/to-clean-keyword "Testing  This")))
    (is (= :testing-this (sut/to-clean-keyword "Testing This")))
    (is (= :testing-this (sut/to-clean-keyword "TestingThis")))
    (is (= :testingthis (sut/to-clean-keyword "TESTINGTHIS")))
    (is (= :testing-this (sut/to-clean-keyword "TESTING THIS")))
    (is (= :testing-this (sut/to-clean-keyword "TESTING_THIS")))
    (is (= :testing-this (sut/to-clean-keyword "Testing-This")))
    (is (= :testing--this (sut/to-clean-keyword "Testing--This")))
    (is (= :testing-this (sut/to-clean-keyword "TestingThis")))
    (is (= :testing-this (sut/to-clean-keyword "testingThis")))
    (is (= :testing-this (sut/to-clean-keyword "testing This")))
    (is (= :testing--this (sut/to-clean-keyword "testing__this")))
    (is (= :testing-this (sut/to-clean-keyword "testing_this")))
    (is (= :testing-this (sut/to-clean-keyword "testing this")))
    (is (= :testing-this (sut/to-clean-keyword " testing this "))))

  (testing "throws an error when result would be an empty string"
    (is (thrown? clojure.lang.ExceptionInfo (sut/to-clean-keyword "[]")))
    (is (thrown? clojure.lang.ExceptionInfo (sut/to-clean-keyword "こんにちは")))
    (is (thrown? clojure.lang.ExceptionInfo (sut/to-clean-keyword "漢")))))
