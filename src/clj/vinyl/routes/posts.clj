(ns vinyl.routes.posts
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.coercions :refer [as-int]]
            [vinyl.logic.posts :as posts]
            [vinyl.db :as db]
            [clojure.java.io :as io]))

(defroutes protected-routes
  (POST "/api/posts" {body :body :as request}
    (let [{:keys [title content]} body
          user (:user request)]
      (posts/create user title content)))
  (POST "/api/files" {:keys [params]}
    (let [{:keys [tempfile filename]} (get params "file")]
      (io/copy tempfile (io/file filename)))))

(defroutes routes
  (GET "/api/posts" []
    {:status 200
     :body (map posts/format-post (db/get-posts))})
  (GET "/api/posts/:id" [id :<< as-int]
    {:status 200
     :body (first (db/get-posts-by-id {:id id}))}))
