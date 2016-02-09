(ns vinyl.views.home
  (:require [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [vinyl.state :refer [state]]
            [vinyl.components :refer [alert card]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def url "api/posts")

(defn index []
  (r/create-class
    {:component-will-mount
      (fn []
        (go
          (let [{:keys [body status]} (<! (http/get url))]
            (if (contains? body :error)
              (swap! state assoc-in [:error (:error body)])
              (swap! state assoc-in [:posts] body)))))
     :reagent-render
      (fn []
        (let [{:keys [posts error]} @state]
          [:div
            [:div {:class "wrapper"}
              [:div {:class "header jumbotron text-center"}
                [:img {:src "/images/logo.png" :srcSet "/images/logo@2x.png 2x"}]]
              [:div {:class "nav"}
                [:ul {:class "text-center list-inline"}
                  [:li
                    [:a {:class "no-link" :href "/"} "Home"]]
                  [:li
                    [:a {:class "no-link" :href "/travel"} "Travel"]]
                  [:li
                    [:a {:class "no-link" :href "/hikes"} "Hikes"]]
                  [:li
                    [:a {:class "no-link" :href "/gear"} "Gear"]]]]
              [:div {:id "main" :class "container-fluid bg-gray home min-full-height"}
                [:div {:class "row"}
                  [:div {:class "col-xs-12 col-sm-push-9 col-sm-3"}
                    [:div {:class "sidebar"}
                      [:div {:class "section"}
                        [:blockquote
                          [:p "It doesn’t matter where you are, you are nowhere compared to where you can go"]
                          [:footer
                            [:cite {:title ""} "Bob Proctor"]]]]
                      [:div {:class "section text-center"}
                        [:h4 "Travels"]
                        [:ul {:class "list-unstyled"}
                          [:li "New Zealand"]
                          [:li "Australia"]
                          [:li "Tasmania"]
                          [:li "California"]
                          [:li "New York"]]]]]
                  (for [post posts]
                    ^{:key (:id post)}
                     [:div {:class "col-xs-12 col-sm-pull-3 col-sm-3"}
                       [card post]])]]
              [:div {:class "push bg-gray"}]]
            [:footer {:class "bg-gray footer text-center"}
              "Made with "
              [:i {:style {:color "#f00"} :class "fa fa-heart"}]
              " by Sean for Alisha"]]))}))
