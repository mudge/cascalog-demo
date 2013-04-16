(defproject model "1.0.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [backtype/dfs-datastores "1.1.3"]
                 [org.apache.hadoop/hadoop-core "0.20.2-dev" :exclusions [org.slf4j/slf4j-api
                                                                          org.slf4j/slf4j-log4j12]]
                 [cheshire "5.0.1"]]
  :aot [model.core])
