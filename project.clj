(defproject clj-aws-tools "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main clj-aws-tools.main
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [amazonica             "0.3.141"
                  :exclusions [com.amazonaws/aws-java-sdk]]
                 [com.amazonaws/aws-java-sdk-core "1.11.555"]
                 [com.amazonaws/aws-java-sdk-ec2 "1.11.555"]
                 [com.amazonaws/aws-java-sdk-resourcegroupstaggingapi "1.11.555"]
                 [org.clojure/tools.cli "0.4.2"]
                 [clj-time              "0.15.0"]]
  :aot :all
  :repl-options {:init-ns clj-aws-tools.core})
