(ns vinyl.logic.posts-test
  (:require [clojure.test :refer :all]
            [vinyl.logic.posts :refer :all]
            [vinyl.db :as db]))

(defn get-posts-columns []
  (let [rows (db/get-column-names-by-table {:table_name "posts"})]
    (->> rows
         (map #(get % :column_name))
         (map #(keyword %))
         (filter #(not= :id %))
         (filter #(not= :created_at %)))))

(def db-columns (get-posts-columns))
(def nil-post-params {:user {:id 1} :title nil :content nil :cover-image-url nil :is-draft false})

(deftest test-post-logic
  (testing "insert params should match db column names"
    (let [insert-params (build-insert-params nil-post-params)]
      (is (= db-columns (keys insert-params)))))

  (testing "nil params should return 422 status"
    (let [response (create nil-post-params)]
      (is (= (:status response) 422)))))
