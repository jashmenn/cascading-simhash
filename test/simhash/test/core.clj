
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
   [java.math BigInteger])
   )

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

(defn find-n-minhashes
  ([n tokens] 
     (find-n-minhashes n tokens (PriorityQueue. (inc n)) nil nil))
  ([n tokens results min max]
     (if (seq tokens) 
       (let [token (first tokens)
             token-hash (sha1-bigint token)
             new-min (apply least    (filter identity [min token-hash]))
             new-max (apply greatest (filter identity [max token-hash]))
             changed (or (not (= min new-min)) (not (= max new-max)))]
         (if changed
           )
         (recur n (rest tokens) results new-min new-max))
       results ;; todo modify results
       )))

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
