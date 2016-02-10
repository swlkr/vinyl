(ns vinyl.routes.photos
  (:require [compojure.core :refer [POST defroutes]]
            [vinyl.config :as config]
            [aws.sdk.s3 :as s3]))

(defn upload-photo [filename file content-type]
  (s3/put-object
    (:cred config/s3)
    (:bucket config/s3)
    filename
    file
    {:content-type content-type}
    (s3/grant :all-users :read))
  {:status 200
   :body {:url (str "//s3.amazonaws.com/" (:bucket config/s3) "/" filename)}})

(defroutes protected-routes
  (POST "/api/photos" {params :params}
    (let [{:keys [filename tempfile content-type]} (get params "file")]
      ; save to s3
      (upload-photo filename tempfile content-type))))
      ; save to database files table
