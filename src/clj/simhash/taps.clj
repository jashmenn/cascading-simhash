
(ns simhash.taps
  (:use 
   [simhash core util]
   [cascalog api testing]
   [clojure.test])
  (:require 
   [cascalog [workflow :as w] [vars :as v] [ops :as c]]))


