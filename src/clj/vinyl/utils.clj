(ns vinyl.utils)

(defn parse-int [str fallback]
  (try
    (Integer/parseInt str)
    (catch java.lang.NumberFormatException e
      fallback)))

(defn nil-val? [[k v]]
  (nil? v))

(defn filter-nil-values [data]
  (map (fn [x] (get x 0)) (filter nil-val? data)))

(defn remove-keys [data keys]
  (apply dissoc data keys))

(defn dissoc-nil-values [data]
  (let [keys (filter-nil-values data)]
    (remove-keys data keys)))
