
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
  :aot [simhash.core])

;; examples.SimpleSimhash depends on the gen-class in simhash.core,
;; but lein *always* does a `lein javac` before a `lein compile` so
;; lein compile always fails (because lein javac can't find
;; simhash.core).
;;  
;; hook in to leiningen.compile and call clojure.core/compile on
;; simhash.core before lein javac compiles the java classes. ew.
(use '[leiningen.core :only [prepend-tasks]]
     '[leiningen.compile :only [eval-in-project]])
(prepend-tasks 
 #'leiningen.compile/compile
 (fn [project] 
   (binding [leiningen.compile/*skip-auto-compile* true]
     (eval-in-project project `(clojure.core/compile 'simhash.core)))))
