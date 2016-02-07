(ns vinyl.utils
  (:require [clojure.string :refer [join]]))

(defn build-url [& parts]
  (join "/" parts))
