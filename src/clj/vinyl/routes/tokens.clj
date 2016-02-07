(ns vinyl.routes.tokens
  (:require [compojure.core :refer [POST defroutes]]
            [vinyl.logic.tokens :refer [generate-token]]
            [vinyl.logic.users :refer [get-user password-is-valid?]]))

(defn create-token [params]
  (let [{:keys [email password]} params
        user (get-user email)
        {:keys [id email]} user]
    (if (password-is-valid? (:password user) password)
      {:status 200
       :body {:access-token (generate-token {:id id :email email})
              :user {:id id :email email}}}
      {:status 401
       :body {:error "Sorry :( can't log you in! Invalid email or password"}})))

(defroutes routes
  (POST "/api/tokens" {body :body}
    (create-token body)))
