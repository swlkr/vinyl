(ns vinyl.api
  (:require [cljs-http.client :as http]))

(defn get [url headers]
  (http/get url {:headers headers}))

(defn post [url params & headers]
  (http/post url {:json-params params :headers (first headers)}))
