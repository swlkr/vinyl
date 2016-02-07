(ns vinyl.logic.users
  (:require [crypto.password.scrypt :refer [encrypt check]]
            [vinyl.db :as db]))

(defn hash-password [plaintext-password]
  (encrypt plaintext-password))

(defn password-is-valid? [password plaintext]
  (if (some nil? [password plaintext])
    false
    (check plaintext password)))

(defn get-user [email]
  (first (db/get-users-by-email {:email email})))
