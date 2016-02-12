(ns vinyl.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [vinyl.handler :refer :all]
            [cheshire.core :as json]))

(def invalid-post {:content nil})

(deftest test-app
  (testing "get root route"
    (let [response (app (mock/request :get "/api/status"))]
      (is (= (:status response) 200))
      (is (= (:body response) "{\"status\":\"alive\"}"))))

  (testing "unauthorized create post"
    (let [response (app (mock/request :post "/api/posts"
                                      {:content-type "application/json"
                                       :body (json/generate-string invalid-post)}))]
      (is (= 401 (:status response))))))
