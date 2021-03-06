(ns vinyl.routes.posts
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.coercions :refer [as-int]]
            [vinyl.logic.posts :as posts]
            [vinyl.db :as db]))

(defroutes protected-routes
  (POST "/api/posts" {body :body :as request}
    (let [user (:user request)]
      (posts/create (merge body {:user user})))))

(defroutes routes
  (GET "/api/posts" []
    {:status 200
     :body (map posts/format-post (db/get-posts))})
  (GET "/api/posts/:id" [id :<< as-int]
    {:status 200
     :body (first (db/get-posts-by-id {:id id}))}))
