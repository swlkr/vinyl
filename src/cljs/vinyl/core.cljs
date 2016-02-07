(ns vinyl.core
    (:require [reagent.core :as r]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs-http.client :as http]
              [cljs.core.async :refer [<!]]
              [vinyl.state :refer [state]]
              [vinyl.components :refer [alert]]
              [vinyl.views.posts :as posts]
              [vinyl.views.login :refer [login]]
              [vinyl.utils :refer [build-url]])
    (:require-macros [cljs.core.async.macros :refer [go]]))

;; -------------------------
;; Views

(defn post-link [id title]
  [:li
    [:a {:href (str "/posts/" id)} title]])

(defn home-page []
  (r/create-class
    {:component-will-mount
      (fn []
        (let [url (build-url "api" "users" "2" "posts")]
          (go
            (let [{:keys [body status]} (<! (http/get url {:with-credentials? false}))]
              (if (contains? body :error)
                (swap! state assoc-in [:error (:error body)])
                (swap! state assoc-in [:posts] body))))))
     :reagent-render
      (fn []
        (let [{:keys [posts error]} @state]
          [:div
            [:h2 "Adventure Walker"]
            [alert error]
            (for [post posts]
              (let [{:keys [id title]} post]
                ^{:key id} [post-link id title]))]))}))

(defn about-page []
  [:div [:h2 "About vinyl"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

(secretary/defroute "/posts/new" []
  (session/put! :current-page #'posts/new))

(secretary/defroute "/posts/:id" [id]
  (session/put! :current-page #'posts/show))

(secretary/defroute "/login" []
  (session/put! :current-page #'login))

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [current-page]
    (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
