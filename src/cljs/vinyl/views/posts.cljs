(ns vinyl.views.posts
  (:require [cljs.core.async :refer [<!]]
            [vinyl.components :refer [alert icon form-input button card]]
            [vinyl.state :refer [state]]
            [vinyl.api :as api]
            [vinyl.util :as util]
            [reagent.session :as session]
            [accountant.core :as accountant]
            [markdown.core :refer [md->html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn on-title-change [e]
  (swap! state assoc :title (-> e .-target .-value)))

(defn on-content-change [e]
  (swap! state assoc :content (-> e .-target .-value)))

(defn add-post [post]
  (swap! state update-in [:posts] conj post))

(defn add-post-and-go-home [post]
  (add-post post)
  (accountant/navigate! "/"))

(defn on-save-click []
  (let [params (select-keys @state [:title :content :cover-image-url])]
    (go
      (let [{:keys [body status]} (<! (api/post "/api/posts" params))
            {:keys [error]} body]
        (if (= status 200)
          (add-post-and-go-home body)
          (swap! state assoc :error error))))))

(defn find-post-by-id [id posts]
  (first
    (filter
      (fn [p]
        (= (:id p) id))
      posts)))

(defn swap-photo! [url]
  (let [{:keys [content photos]} @state]
    (swap! state update-in [:photos] conj url)
    (if (> (count photos) 0)
      (swap! state assoc :content (str content "\n\n![image](" url ")"))
      (swap! state assoc :cover-image-url url))))

(defn get-file-preview [e callback]
  (let [file (first (array-seq (-> e .-target .-files)))
        file-reader (js/FileReader.)]
    (set! (.-onload file-reader)
      (fn [ev]
        (callback ev file)))
    (.readAsDataURL file-reader file)))

(defn file-preview-callback [e file]
  (go
    (let [{:keys [body status]} (<! (api/upload "/api/photos" file))]
      (if (nil? (:error body))
        (swap-photo! (:url body))
        (swap! status assoc :error (:error body))))))

(defn on-file-change [e]
  (get-file-preview e file-preview-callback))

(defn show []
  (let [id (js/parseInt (session/get :current-post) 10)
        post (find-post-by-id id (:posts @state))
        {:keys [title content]} post]
    [:div {:class "container"}
      [:div {:class "header"}
        [:h1 {:class "text-center"} title]]
      [:div {:class "row"}
        [:div {:class "col-xs-10 col-xs-offset-1"}
          [:div {:class "post responsive-images" :dangerouslySetInnerHTML {:__html (md->html content)}}]]]]))

(defn new []
  (let [{:keys [title content photos error posts]} @state]
    [:div {:class "container-fluid full-height"}
      [:div {:class "row full-height"}
        [:div {:class "col-sm-5"}
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
          [button "Save" {:class "btn btn-primary" :on-click on-save-click}]
          [:span {:class "pull-right"}
            [:span {:class "btn btn-primary btn-file"}
              [:input {:type "file"
                       :on-change on-file-change}]
              [icon "camera"] " Add Photo"]]]
        [:div {:class "col-sm-7 full-height p-t-lg bg-gray"}
          [:div {:class "row"}
            [:div {:class "col-sm-5"}
              [card {:title title
                     :excerpt (util/get-excerpt content)
                     :cover-image-url (first photos)}]]
            [:div {:class "col-sm-7"}
              [:h2 {:class "text-center"} title]
              [:div {:class "responsive-images" :dangerouslySetInnerHTML {:__html (md->html content)}}]]]]]]))
