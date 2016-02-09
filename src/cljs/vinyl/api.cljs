(ns vinyl.api
  (:require [cljs-http.client :as http]
            [reagent.session :as session]))

(defn get [url headers]
  (http/get url {:headers headers}))

(defn format-headers [headers]
  (let [access-token (session/get :access-token)]
    (if (nil? access-token)
      headers
      (merge headers {"Authorization" access-token}))))

(defn post [url params & rest]
    (http/post url {:json-params params :headers (format-headers (first rest))}))

(defn upload [url file & rest]
  (http/post url {:multipart-params [["file" file]]
                  :headers (format-headers (first rest))}))
