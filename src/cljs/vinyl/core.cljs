(ns vinyl.core
    (:require [reagent.core :as r]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [cljs-http.client :as http]
              [cljs.core.async :refer [<!]]
              [vinyl.views.posts :as posts]
              [vinyl.views.login :refer [login]]
              [vinyl.views.home :as home])
    (:require-macros [cljs.core.async.macros :refer [go]]))

;; -------------------------
;; Views

(defn about-page []
  [:div [:h2 "About vinyl"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home/index))

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
