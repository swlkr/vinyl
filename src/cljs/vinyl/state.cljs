(ns vinyl.state
  (:require [reagent.core :as r]))

(def state
  (r/atom
    {:error nil
     :title nil
     :content nil
     :tags []
     :email nil
     :password nil
     :posts []}))
