(defproject cascading-simhash "1.0.0-SNAPSHOT"
  :description "Calculate simple simhashing in Hadoop"
  :dependencies 
  [[org.clojure/clojure "1.2.0"]
   [org.clojure/clojure-contrib "1.2.0"]
   [cascalog "1.7.0-SNAPSHOT"]
   [net.htmlparser.jericho/jericho-html "3.1"]]
  :dev-dependencies 
  [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
   [swank-clojure "1.2.1"]]
  :repositories
  {"clojars" "http://clojars.org/repo"}
  :jvm-opts ["-Xmx1400m"] 
  :compile-path "build/classes"
  :target-dir "build"
  :java-source-path "src/java"
  :source-path "src/clj"
  :aot [simhash.test.core])
