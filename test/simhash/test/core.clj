
(ns simhash.test.core
  (:use 
   [simhash core util]
   [cascalog api testing]
   [clojure.test]
   [clojure.contrib greatest-least])
  (:require 
   [simhash [taps :as t] [ops :as ops]]
   [cascalog [workflow :as w] [vars :as v] [ops :as c]]
   [clojure.contrib.str-utils :as stu])
  (:import 
   [java.util StringTokenizer PriorityQueue]
   [java.security MessageDigest]
   [java.math BigInteger]
   [java.util TreeMap]))

(def testfile "test-resources/test-documents.txt")

(defn test-source [path]
  (<- [?docid ?body]
      ((hfs-textline path) ?line)
      (ops/re-split-op [#"\t" 2] ?line :> ?docid ?body)
      (:distinct false)))

(defn tokenize 
  "tokenize into bi-grams (sliding window)"
  [body]
  (map
   (fn [tokens] (stu/str-join " " tokens))
   (partition 2 1 (enumeration-seq (StringTokenizer. body)))))

(deftest minhash-finds-dog-cluster-at-one
  (test?- 
   [["13fa516911cb3688e24f5624714487ff6852a7e1" "DocD" "see spot run"]
    ["96b52e0356de4547e75250020a38ad0fc233ce3f" "DocA" "my dog has fleas"]
    ["96b52e0356de4547e75250020a38ad0fc233ce3f" "DocB" "my dog has fleas"]
    ["96b52e0356de4547e75250020a38ad0fc233ce3f" "DocC" "my dog has hair"]]
   (simhash-q (test-source testfile) 1 tokenize))) 

(deftest minhash-finds-fleas-cluster-at-two
  (test?- 
   [["302800f65127301b7f16ac09c13cac7bd491db05" "DocA" "my dog has fleas"]
    ["302800f65127301b7f16ac09c13cac7bd491db05" "DocB" "my dog has fleas"]
    ["6acb9024e2522478f6de07b4766b34e72c4e40ec" "DocD" "see spot run"]
    ["9f7efc7e7bb4e29d9a508d32985b64cba3edffde" "DocC" "my dog has hair"]]
   (simhash-q (test-source testfile) 2 tokenize))) 

(comment
  "here are some other ways you can run this:"

  (.complete 
   (simhash 
    (test-source testfile) (stdout) 2 
    #(tokenize %)))

  (minhash 2 (seq (tokenize "hello my dog has fleas")))
  (time (minhash+ (slurp testfile) 2 tokenize)))
