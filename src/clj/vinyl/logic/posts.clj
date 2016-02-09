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

(defn format-date [post]
  (let [{:keys [created_at]} post
        date (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") created_at)]
    (assoc post :date date)))

(defn get-excerpt [content]
  (let [end (min (count content) 140)
        excerpt (subs content 0 end)]
    (if (> end 140)
      (str excerpt "...")
      excerpt)))

(defn add-excerpt [post]
  (let [excerpt (get-excerpt (:content post))]
    (assoc post :excerpt excerpt)))

(defn format-post [post]
  (-> post
      (format-date)
      (add-excerpt)))
