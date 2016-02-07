(ns vinyl.db
  (:require [yesql.core :refer [defqueries]]
            [environ.core :refer [env]]
            [vinyl.utils :refer [dissoc-nil-values]]))

(def database-url
  (env :database-url))

(def database-uri
  (java.net.URI. database-url))

(def user-and-password
  (let [user-info (.getUserInfo database-uri)]
    (if (nil? user-info)
      nil
      (clojure.string/split user-info #":"))))

(def database-spec
  (dissoc-nil-values
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :user (get user-and-password 0)
     :password (get user-and-password 1)
     :subname (if (= -1 (.getPort database-uri))
                (format "//%s%s" (.getHost database-uri) (.getPath database-uri))
                (format "//%s:%s%s" (.getHost database-uri) (.getPort database-uri) (.getPath database-uri)))}))

(defqueries "vinyl/sql/users.sql"
  {:connection database-spec})

(defqueries "vinyl/sql/posts.sql"
  {:connection database-spec})
