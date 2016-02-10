(ns vinyl.config
  (:require [envvar.core :refer [env]]))

(def database-url (or (:database-url @env) "postgresql://localhost:5432/adventure_walker"))
(def secret (or (:secret @env) "7Gc3fyrXAZ5D2PS13Ir414so9sWsOErF"))
(def dev? (or (:dev @env) true))
(def port (or (:port @env) "3000"))

; amazon s3
(def s3-access-key (:s3-access-key @env))
(def s3-secret-key (:s3-secret-key @env))
(def s3-credentials {:access-key s3-access-key, :secret-key s3-secret-key})
(def s3-bucket (or (:s3-bucket @env) "vinyl-photos"))
(def s3 {:bucket s3-bucket
         :cred s3-credentials})
