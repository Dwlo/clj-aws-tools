(ns clj-aws-tools.main
  (:require [clj-aws-tools.core  :as    core]
            [clojure.tools.cli   :refer [parse-opts]])
  (:gen-class :main true))

(def cli-options
  [["-h" "--help"]])

(defn -main [& args]
  (let [ action (first (:arguments (parse-opts args cli-options)))]
    (-> (core/run-ec2-backup)
        println)))
