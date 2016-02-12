(ns vinyl.logic.posts
  (:require [vinyl.db :as db]
            [vinyl.utils :refer [filter-nil-values]]))

(defn build-insert-params [params]
  (let [{:keys [user title content cover-image-url is-draft]} params]
    {:user_id (:id user)
     :title title
     :content content
     :cover_image_url cover-image-url
     :is_draft is-draft}))

(defn create [post-params]
  (let [params (build-insert-params post-params)]
    (if (some empty? (map #(str %) (vals params)))
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

(defn format-cover-image-url [post]
  (let [{:keys [cover_image_url]} post]
    (-> post
        (assoc :cover-image-url cover_image_url)
        (dissoc :cover_image_url))))

(defn format-post [post]
  (-> post
      (format-date)
      (add-excerpt)
      (format-cover-image-url)))
