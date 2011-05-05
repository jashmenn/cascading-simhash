
(ns simhash.util
  (:require 
   [clojure.contrib.str-utils :as stu]))

(defn parse-line-into-n-fields 
"split a line into a predicable number of columns
(parse-line-into-n-fields \"a,b,c\" #\",\" 3) -> (\"a\" \"b\" \"c\")
(parse-line-into-n-fields \"a,b,c\" #\",\" 2) -> (\"a\" \"b\")
(parse-line-into-n-fields \"a,b,c\" #\",\" 5) -> (\"a\" \"b\" \"c\" nil nil)"
  [line pattern num-fields]
  (first (partition num-fields num-fields (repeat num-fields nil)
    (stu/re-split pattern line))))

