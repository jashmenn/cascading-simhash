

(ns simhash.tokenizers.html-text
  (:use [simhash util])
  (:require 
   [clojure.contrib.str-utils2 :as su])
  (:import
   [net.htmlparser.jericho TextExtractor StreamedSource Source
     StartTag Tag CharacterReference])
  (:gen-class
   :name simhash.tokenizers.HtmlText
   :methods [#^{:static true} [tokenize [java.lang.CharSequence int] clojure.lang.ISeq]
             #^{:static true} [tokenizer [int] clojure.lang.AFn]]))

(defn tokenize 
"tokenize the text of an HTML document. Streaming/lazy so it can
handle large documents."
  ([body] (tokenize body 3))
  ([body n-gram-size]
     (->> 
      (iterator-seq (.iterator (StreamedSource. body)))
      (filter (fn [seg] 
                (not (or (isa? (class seg) Tag)
                         (isa? (class seg) CharacterReference)))))
      (map (fn [seg] (su/trim (.toString seg))))
      (filter (complement empty?))
      (map (fn [s] (su/split s #"\s+")))
      (flatten)
      (partition n-gram-size 1)
      (map (fn [col] (su/join " " col))))))

(defn -tokenize [body n-gram-size]
  (tokenize body n-gram-size))

(defn -tokenizer [n-gram-size]
  (fn [body] (tokenize body n-gram-size)))

(comment

  (def html-doc (slurp "test-resources/haiku.html"))
  (tokenize html-doc)

  )

