
(ns simhash.test.core
  (:use 
   [simhash core util]
   [cascalog api testing]
   [clojure.test])
  (:require 
   [simhash [taps :as t] [ops :as ops]]
   [cascalog [workflow :as w] [vars :as v] [ops :as c]])
  (:import 
   [java.util StringTokenizer])
  (:gen-class))

(def testfile "test-resources/test-documents.txt")

(defn test-source [path]
  (<- [?docid ?body]
      ((hfs-textline path) ?line)
      (ops/re-split-op [#"\t" 2] ?line :> ?docid ?body)
      (:distinct false)))

(defn tokenize [body]
  (enumeration-seq (StringTokenizer. body)))
; --

(defn minhash+ [body r tokenizer]
  (map
   (fn [t] (prn t) t)
   (iterator-seq (.tokenize tokenizer body)))
  "4")

(w/defmapop [minhash-op [r tokenizer]] [body]
  (minhash+ body r tokenizer))

(defn simhash [source tap-out r tokenizer]
  (compile-flow "simhash" tap-out
    (<- [?minhash ?docid ?body]
        (source ?docid ?body)
        (minhash-op [r tokenizer] ?body :> ?minhash))))

(comment

  (.complete 
   (simhash 
    (test-source testfile) (stdout) 1 tokenize))

  (?<- 
   (stdout)
   [?docid ?body]
   ((test-source testfile) ?docid ?body))

  )

(deftest replace-me ;; FIXME: write
  (is false "No tests have been written."))
