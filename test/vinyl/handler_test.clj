(ns vinyl.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [vinyl.handler :refer :all]
            [cheshire.core :as json]))

(def expected-status-response (json/generate-string {:status "alive"}))
(def expected-posts-response (json/generate-string [{:id 1}]))
(def invalid-post {:content nil})

(deftest test-app
  (testing "get root route"
    (let [response (app (mock/request :get "/api/status"))]
      (is (= 200 (:status response)))
      (is (= expected-status-response (:body response)))))

  (testing "get list of posts"
    (let [response (app (mock/request :get "/api/posts"))]
      (is (= 200 (:status response)))))

  (testing "unauthorized create post"
    (let [response (app (mock/request :post "/api/posts"
                                      {:content-type "application/json"
                                       :body (json/generate-string invalid-post)}))]
      (is (= 401 (:status response))))))
