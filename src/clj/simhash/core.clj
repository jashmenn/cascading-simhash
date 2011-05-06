
(ns simhash.core
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
   [java.util TreeMap])
  (:gen-class
   :name simhash.Simhash
   :methods [#^{:static true} [simhash [Object Object int clojure.lang.AFn] cascading.flow.Flow]]))

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
      (remove-max-to n))
    results))

(defn- xor-keyset [results]
  (let [keys (seq (.keySet results))]
   (reduce #(.xor %1 %2) (first keys) (rest keys))))

(defn minhash
  ([n tokens] 
     (minhash n tokens (TreeMap.)))
  ([n tokens results]
     (if (seq tokens) 
       (recur n (rest tokens) 
         (maybe-store n results (sha1-bigint (first tokens))))
       (xor-keyset results))))

(defn minhash+ [body r tokenizer]
  (bigint-to-hex-string
   (minhash r (tokenizer body))))

(w/defmapop [minhash-op [r tokenizer]] [body]
  (minhash+ body r tokenizer))

(defn simhash-q [source r tokenizer]
  (<- [?minhash ?docid ?body]
        (source ?docid ?body)
        (minhash-op [r tokenizer] ?body :> ?minhash)))

(defn -simhash 
  [source tap-out r tokenizer]
  (compile-flow "simhash" tap-out
                (simhash-q source r tokenizer)))

