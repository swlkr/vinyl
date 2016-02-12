(ns vinyl.handler
  (:require [compojure.core :refer [GET POST wrap-routes defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [vinyl.middleware :refer [wrap-middleware]]
            [ring.middleware.json :as ring-json]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [bunyan.core :as bunyan]
            [vinyl.routes.posts :as posts]
            [vinyl.routes.photos :as photos]
            [vinyl.routes.tokens :as tokens]
            [vinyl.logic.tokens :refer [decode-token]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [vinyl.config :as config]))

; database migrations
(defn load-config []
  {:datastore  (jdbc/sql-database (str "jdbc:" config/database-url))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
 (repl/migrate (load-config)))

(defn rollback []
 (repl/rollback (load-config)))

; api error handling middleware
(defn wrap-fallback-exception [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (let [{:keys [status]} (ex-data e)]
          {:status (or status 500)
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
     [:link {:href "https://fonts.googleapis.com/css?family=Lato:400,300,100,700" :rel "stylesheet"}]
     [:link {:href "https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" :rel "stylesheet"}]
     [:title "Adventure Walker"]
     (include-css "/css/bootstrap.min.css")
     (include-css (if config/dev? "/css/site.css" "/css/site.min.css"))]

    [:body
     mount-target
     (include-js "/js/app.js")]]))

(defroutes protected-api-routes
  posts/protected-routes
  photos/protected-routes)

(defroutes api-routes
  (GET "/api/status" [] {:status 200 :body {:status "alive"}})
  posts/routes
  tokens/routes
  (wrap-routes protected-api-routes wrap-jwt-auth))

(defn wrap-api-middleware [handler]
  (-> handler
      (wrap-multipart-params)))

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

(def app (wrap-reload routes))
