(ns vinyl.components
  (:require [markdown.core :refer [md->html]]))

(defn alert [message type]
  (if (or (nil? message) (empty? message))
    nil
    (let [alert-type (or type "alert-danger")]
      [:div {:class (str "alert " alert-type)} message])))

(defn button [title attrs]
  [:button attrs title])

(defn input [type value placeholder on-change]
  [:input {:type type
           :value value
           :placeholder placeholder
           :class "form-control"
           :on-change on-change}])

(defn textarea [value placeholder on-change]
  [:textarea {:value value
              :placeholder placeholder
              :class "form-control"
              :rows "15"
              :on-change on-change}])

(defn form-input [props]
  (let [{:keys [label type value placeholder on-change]} props]
    (if (= type "textarea")
      [:div {:class "form-group"}
        [:label label]
        [textarea value placeholder on-change]]
      [:div {:class "form-group"}
        [:label label]
        [input type value placeholder on-change]])))

(defn icon [name]
  [:i {:class (str "fa fa-" name)}])

(defn card [props]
  (let [{:keys [id date title excerpt cover-image-url]} props]
    [:div {:class "card"}
      [:h4 {:class "header"} title]
      [:img {:class "img-responsive" :src cover-image-url :alt "cover image"}]
      [:div {:class "body"}
        [:span {:class "responsive-images" :style {:whiteSpace "pre-line"} :dangerouslySetInnerHTML {:__html (md->html excerpt)}}]
        [:a {:href (str "/posts/" id) :class "tiffany-light m-t-lg"} "Continue Reading  "
          [icon "long-arrow-right"]]]
      [:div {:class "footer"}
        [icon "clock-o"] (str " " date)]]))
