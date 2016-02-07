(ns vinyl.logic.tokens
  (:require [clj-jwt.core :refer [verify jwt to-str sign str->jwt]]
            [clj-time.core :refer [now plus days]]
            [environ.core :refer [env]]))

(def claim
  {:iss "self"
   :exp (plus (now) (days 7))
   :iat (now)})

(defn build [payload]
  (merge payload claim))

(defn generate [data secret]
  (-> data jwt (sign :HS256 secret) to-str))

(defn verify-token [token secret]
  (-> token str->jwt (verify secret)))

(defn decode [token secret]
  (if (not (nil? (verify-token token secret)))
    (-> token str->jwt :claims)
    nil))

(defn decode-token [token]
  (if (nil? token)
    nil
    (decode token (env :secret))))

(defn generate-token [payload]
  (generate (build payload) (env :secret)))
