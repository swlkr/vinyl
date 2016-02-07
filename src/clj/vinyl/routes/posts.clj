(ns vinyl.routes.posts
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.coercions :refer [as-int]]
            [vinyl.logic.posts :as posts]
            [vinyl.db :as db]))

(defroutes protected-routes
  (POST "/api/posts" {body :body :as request}
    (let [{:keys [title content]} body
          user (:user request)]
      (posts/create user title content))))

(defroutes routes
  (GET "/api/users/:user_id/posts" [user_id :<< as-int :as request]
    {:status 200
     :body (db/get-posts {:user_id user_id})})
  (GET "/api/posts/:id" [id :<< as-int]
    {:status 200;}
     :body (first (db/get-posts-by-id {:id id}))}))
