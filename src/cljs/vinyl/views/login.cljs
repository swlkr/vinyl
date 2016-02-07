(ns vinyl.views.login
  (:require [cljs.core.async :refer [<!]]
            [vinyl.components :refer [alert form-input button]]
            [vinyl.state :refer [state]]
            [vinyl.utils :refer [build-url]]
            [vinyl.api :as api]
            [reagent.session :as session]
            [accountant.core :as accountant])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn on-email-change [e]
  (swap! state assoc :email (-> e .-target .-value)))

(defn on-password-change [e]
  (swap! state assoc :password (-> e .-target .-value)))

(defn log-user-in [response]
  (session/put! :access-token (get "access-token" response))
  (session/put! :user (get "user" response))
  (accountant/navigate! "/posts/new"))

(defn on-login-click [e]
  (let [params (select-keys @state [:email :password])]
    (swap! state assoc-in [:error] nil)
    (go
      (let [response (<! (api/post "api/tokens" params))
            {:keys [body status]} response]
        (if (= status 401)
          (swap! state assoc-in [:error] (:error body))
          (log-user-in body))))))

(defn login []
  (let [{:keys [error email password]} @state]
    [:div {:class "container"}
      [:div {:class "row"}
        [:div {:class "col-xs-4 col-xs-offset-4"}
          [:h2 "Login"]
          [alert error]
          [form-input {:label "Email"
                       :type "email"
                       :value email
                       :placeholder "your-email@gmail.com"
                       :on-change on-email-change}]
          [form-input {:label "Password"
                       :type "password"
                       :value password
                       :on-change on-password-change}]
          [button "Login" {:class "btn btn-primary"
                           :on-click on-login-click}]]]]))
