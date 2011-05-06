
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

; --

(defn- remove-max-to [col size]
  (while (> (.size col) size)
    (.remove col (.lastKey col)))
  col)

(defn- maybe-store 
"we don't need to store every hash, just the n least
if results has less than n elements, insert this element
if token-hash is less than max(results) then
  insert token-hash and remove the max
returns results, possibly modified"
  [n results token-hash]
  (if (or (< (.size results) n)
          (> (.lastKey results) token-hash))
    (doto results
      (.put token-hash true)
      (remove-max-to n))))

(defn- xor-keyset [results]
  (let [keys (seq (.keySet results))]
   (reduce #(.xor %1 %2) (first keys) (rest keys))))

(defn find-n-minhashes
  ([n tokens] 
     (find-n-minhashes n tokens (TreeMap.)))
  ([n tokens results]
     (if (seq tokens) 
       (recur n (rest tokens) 
         (maybe-store n results (sha1-bigint (first tokens))))
       (xor-keyset results))))

(comment

  (find-n-minhashes 2 (seq (tokenize "hello my dog has fleas")))

  )

(defn minhash+ [body r tokenizer]
  (doall
   (map
    (fn [t] (prn t) t)
    (seq (tokenizer body))))
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
    (test-source testfile) (stdout) 1 
    #(tokenize %)))

  (?<- 
   (stdout)
   [?docid ?body]
   ((test-source testfile) ?docid ?body))

  )

(deftest replace-me ;; FIXME: write
  (is false "No tests have been written."))
