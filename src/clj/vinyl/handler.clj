(ns vinyl.handler
  (:require [compojure.core :refer [GET wrap-routes defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [vinyl.middleware :refer [wrap-middleware]]
            [ring.middleware.json :as ring-json]
            [environ.core :refer [env]]
            [ring.middleware.reload :refer [wrap-reload]]
            [bunyan.core :as bunyan]
            [vinyl.routes.posts :as posts]
            [vinyl.routes.tokens :as tokens]
            [vinyl.logic.tokens :refer [decode-token]]))

; api error handling middleware
(defn wrap-fallback-exception [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (let [{:keys [status]} (ex-data e)]
          {:status (or status :500)
           :headers {"Content-Type" "application/json"}
           :body {:error (.getMessage e)}})))))

; json web token middleware
(defn wrap-jwt-auth [handler]
  (fn [request]
    (let [{:keys [headers]} request
          token (decode-token (get headers "authorization"))]
      (if (nil? token)
        (throw
          (ex-info "This is not the droid you're looking for" {:status 401}))
        (-> (assoc request :user (select-keys token [:id :email]))
            handler)))))

(def mount-target
  [:div#app
    [:p "Loading..."]])

(def loading-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css "/css/bootstrap.min.css")
     (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))]
    [:body
     mount-target
     (include-js "/js/app.js")]]))

(defroutes protected-api-routes
  posts/protected-routes)

(defroutes api-routes
  posts/routes
  tokens/routes
  (wrap-routes protected-api-routes wrap-jwt-auth))

(defn wrap-api-middleware [handler]
  (-> handler
      (wrap-reload)))

(defroutes site-routes
  (GET "/" [] loading-page)
  (GET "/*" [] loading-page)
  (resources "/"))

(defroutes routes
  (-> api-routes
      (wrap-fallback-exception)
      (ring-json/wrap-json-response)
      (ring-json/wrap-json-body {:keywords? true})
      (wrap-routes wrap-api-middleware))
  (-> site-routes
      (wrap-routes wrap-middleware))
  (not-found "Not found"))

(def app routes)
