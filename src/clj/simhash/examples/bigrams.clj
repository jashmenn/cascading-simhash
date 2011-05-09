
(comment "
An example of how to use simhash with cascalog.

Usage:

  lein compile
  lein run -m simhash.examples.bigrams test-resources/test-documents.txt 

Note two things about this example:

  * Your source can be a regular cascalog query, and isn't limited to
    being just a Tap, as in the java example.
  * Your `tokenize` function *must* be a compiled function. That means
    you need to gen-class and aot whatever class contains your
    tokenizer.

")
(ns simhash.examples.bigrams
  (:use 
   [simhash core util]
   [cascalog api testing])
  (:require 
   [simhash [taps :as t] [ops :as ops]]
   [clojure.contrib.str-utils :as stu])
  (:gen-class))

(defn my-source [path]
  (<- [?docid ?body]
      ((hfs-textline path) ?line)
      (ops/re-split-op [#"\t" 2] ?line :> ?docid ?body)
      (:distinct false)))

(defn tokenize 
  "tokenize into bi-grams (sliding window)"
  [body]
  (map
   (fn [tokens] (stu/str-join " " tokens))
   (partition 2 1 (stu/re-split #"\s+" body))))

(defn -main [& args]
  (?- (stdout) 
      (simhash-q (my-source (first args))
                 2 ;; number of minhashes
                 tokenize)))
