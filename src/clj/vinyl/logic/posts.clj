(ns vinyl.logic.posts
  (:require [vinyl.db :as db]))

(defn insert-params [user title content]
  {:user_id (:id user)
   :title title
   :content content})

(defn create [user title content]
  (let [params (insert-params user title content)]
    (if (or
          (some nil? [user title content])
          (some empty? [title content]))
      {:status 422
       :body {:error "You need to fill in both title and body"}}
      {:status 200
       :body (db/insert-post<! params)})))
