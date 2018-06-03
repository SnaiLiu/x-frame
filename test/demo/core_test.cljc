(ns demo.core-test
  "测试核心逻辑，

  * 因为自己核心逻辑通常没有用js实现，所以没有展示js测试"
  (:require
    [clojure.test :refer :all]
    [demo.core :as core]))


(deftest test-user-info
  (is (= (core/user-info {:db {}} "testname")
         {:db {} :request {:method :get, :uri "/hjd/testname", :resp-event :user-info-resp} })))

(defn p
  [a b]
  (+ a b))

(deftest test-plus
  (is (= (p 1 2)
         3)))