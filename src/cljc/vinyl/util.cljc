(ns vinyl.util)

(defn get-excerpt [content]
  (let [end (min (count content) 140)
        excerpt (subs (or content "") 0 end)]
    (if (= end 140)
      (str excerpt "...")
      excerpt)))
