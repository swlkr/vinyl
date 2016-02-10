(ns vinyl.server
  (:require [vinyl.handler :refer [app]]
            [vinyl.config :as config]
            [vinyl.utils :refer [parse-int]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (let [port (parse-int config/port 3000)]
    (run-jetty app {:port port :join? false})))
