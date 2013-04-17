(defproject processor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [model "0.0.1-SNAPSHOT"]
                 [backtype/dfs-datastores-cascading "1.2.0"]
                 [cheshire "5.0.0"]
                 [cascalog "1.10.0"]]
  :jvm-opts ["-Xmx4g"]
  :profiles { :dev {:dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]]}})
