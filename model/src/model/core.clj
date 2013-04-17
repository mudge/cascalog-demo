(ns model.core
  (:require [cheshire.core :as json])
  (:import [backtype.hadoop.pail Pail PailSpec PailStructure]))


(defn deserialize [obj]
  (-> obj
    (String. "UTF-8")
    (json/parse-string true)))

(defn serialize [obj]
  (-> obj
    json/generate-string
    (.getBytes "UTF-8")))

;; [ json-pail ]-----------------------------

(gen-class
  :name model.JsonPailStructure
  :implements [backtype.hadoop.pail.PailStructure]
  :prefix "*")

(defn *deserialize [this byte-array]
  (deserialize byte-array))

(defn *serialize [this obj]
  (serialize obj))

(defn *getType [this]
  (class Object))

(defn *getTarget
  [this obj]
  ["data"])

(defn *isValidTarget
  [this paths]
  (= "data" (first paths)))

;; helpers

(defn create-json-pail
  "Creates a json pail at root"
  [root]
  (Pail/create root (PailSpec. "SequenceFile" {} (model.JsonPailStructure.))))

(defn get-or-create-json-pail
  [root]
  (try (Pail. root)
    (catch Exception e
      (create-json-pail root))))
