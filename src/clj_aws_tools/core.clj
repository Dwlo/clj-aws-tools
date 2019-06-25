(ns clj-aws-tools.core
  (:require [amazonica.aws.ec2                      :as ec2]
            [amazonica.aws.resourcegroupstaggingapi :as rgtgsapi]
            [clojure.string                         :as str]
            [clj-time.local                         :as ctl]
            [clj-time.format                        :as ctf]))

;;; variables

(def time-formatter (ctf/formatter "yyyyMMdd_HHmm"))

;;; functions
  (defn stop-instances [ids]
  (ec2/stop-instances :instance-ids ids))

  (defn start-instances "here is some doc" [ids]
  (ec2/start-instances :instance-ids ids))

(defn get-instance-state [ids]
  (ec2/describe-instance-status :instance-ids ids))

(defn get-tagged-resources
  "Returns a list of resources tagged with tag-key and tag-value"
  [tag-key tag-value]
  (->> (rgtgsapi/get-resources :tag-filters [{:key tag-key :values [tag-value]}])
       :resource-tag-mapping-list))

(defn get-resource-pair
  "Returns a [resource-type id] pair from an ARN"
  [arn]
  (str/split (last (str/split arn #":")) #"/"))

(defn get-backupable-instances
  []
  (->> (get-tagged-resources "Backupable" "True")
       (filter (fn [e] (and (contains? e :tags)
                            (= "instance" (->> (:resource-arn e)
                                               get-resource-pair
                                               first)))))))

(defn create-image [instance-id no-reboot? image-name]
  (ec2/create-image {:instance-id instance-id :no-reboot no-reboot? :name image-name}))

(defn- extract-tag-value [tag-key tags]
  (->> (filter #(= tag-key (:key %)) tags)
       (apply :value)))

(defn tag-ami [ami-id name]
  (rgtgsapi/tag-resources {:resource-arn-list [(str "arn:aws:ec2:us-east-1:591122169971:image/" ami-id)]
                           :tags              {"Name" name "AutoBackup" "True"}}))

(defn run-ec2-backup
  "Backups all tagged instances with key Backupable having value True"
  []
  (as-> (get-backupable-instances) $
       (map (fn [e] [(second (get-resource-pair (:resource-arn e)))
                     true
                     (-> (extract-tag-value "Name" (:tags e))
                         (str "_" (ctf/unparse time-formatter  (ctl/local-now))))]) $)
       (pmap #(apply create-image %) $)
       (map :image-id $)
       (ec2/describe-images {:image-ids $})
       (:images $)
       (map (fn [e] [(:image-id e) (:name e)]) $)
       (pmap #(apply tag-ami %) $)))
