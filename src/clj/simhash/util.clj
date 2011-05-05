
(ns simhash.util
  (:require 
   [clojure.contrib.str-utils :as stu])
  (:import [java.security MessageDigest]
           [java.io File]
           [java.math BigInteger]))

(defn parse-line-into-n-fields 
"split a line into a predicable number of columns
(parse-line-into-n-fields \"a,b,c\" #\",\" 3) -> (\"a\" \"b\" \"c\")
(parse-line-into-n-fields \"a,b,c\" #\",\" 2) -> (\"a\" \"b\")
(parse-line-into-n-fields \"a,b,c\" #\",\" 5) -> (\"a\" \"b\" \"c\" nil nil)"
  [line pattern num-fields]
  (first (partition num-fields num-fields (repeat num-fields nil)
    (stu/re-split pattern line))))

(defn byte-to-hex-string
  "Converts the lower 16 bits of b to into a hex string"
  [b]
  (let [s (Integer/toHexString (bit-and 0xff b))]
    (if (= (count s) 1) (str "0" s) s)))

(defn bytes-to-hex-string
  "Converts a Java byte array into a hex string"
  [bs]
  (apply str (map byte-to-hex-string (seq bs))))

(defn sha1-bytes [obj]
  (let [s (with-out-str (print obj))
        bytes (.getBytes ^String s)]
    (.digest (MessageDigest/getInstance "SHA1") bytes)))

(defn sha1-bigint
  "returns a BigInteger of the hash representation of obj"
  [obj]
  (BigInteger. ^bytes (sha1-bytes obj)))

(defn bigint-to-hex-string [bi]
  (bytes-to-hex-string (.toByteArray bi)))

