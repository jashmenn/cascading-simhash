
(ns simhash.test.core
  (:use 
   [simhash core util]
   [cascalog api testing]
   [clojure.test])
  (:require 
   [simhash [taps :as t] [ops :as ops]]
   [cascalog [workflow :as w] [vars :as v] [ops :as c]]))

(def testfile "test-resources/test-documents.txt")

(defn test-source [path]
  (<- [?docid ?body]
      ((hfs-textline path) ?line)
      (ops/re-split-op [#"\t" 2] ?line :> ?docid ?body)
      (:distinct false)))

(comment

 (?<- 
  (stdout)
  [?docid ?body]
  ((test-source testfile) ?docid ?body))

 )

(deftest replace-me ;; FIXME: write
  (is false "No tests have been written."))
