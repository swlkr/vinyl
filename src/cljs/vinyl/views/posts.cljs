(ns vinyl.views.posts
  (:require [cljs.core.async :refer [<!]]
            [vinyl.components :refer [alert form-input button]]
            [vinyl.state :refer [state]]
            [vinyl.api :as api])
  (:require-macros [cljs.core.async.macros :refer [go]]))
(defn on-title-change [e]
  (swap! state assoc :title (-> e .-target .-value)))

(defn on-content-change [e]
  (swap! state assoc :content (-> e .-target .-value)))

(defn on-save-click []
  (let [params (select-keys @state [:title :content])]
    (go
      (let [{:keys [body status]} (<! (api/post "/api/posts" params))]
        (if (contains? body :error)
          (swap! state assoc-in [:error] (:error body))
          (swap! state assoc-in [:posts] body))))))

(defn show [id]
  [:header
    [:h1 {:class "text-center"}]])

(defn new []
  (let [{:keys [title content error posts]} @state]
    [:div {:class "container"}
      [:h2 "New Post"]
      [alert error]
      [form-input {:label "Title"
                   :type "text"
                   :value title
                   :placeholder "Title goes here"
                   :on-change on-title-change}]
      [form-input {:label "Body"
                   :type "textarea"
                   :value content
                   :placeholder "Blog post goes here"
                   :on-change on-content-change}]
      [button "Save" {:class "btn btn-primary" :on-click on-save-click}]]))
