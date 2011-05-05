
(ns simhash.ops
  (:use 
   [simhash core util]
   [cascalog api testing]
   [clojure.test])
  (:require 
   [cascalog [workflow :as w] [vars :as v] [ops :as c]]))

(w/defmapop [re-split-op [pattern num-fields]] [str]
  (parse-line-into-n-fields (.toString str) pattern num-fields))
