(ns processor.tap
  (:use [model.core]
        [cascalog.api]
        [cascalog.io :only [with-fs-tmp]])
  (:import [backtype.hadoop.pail Pail]
           [backtype.cascading.tap PailTap PailTap$PailTapOptions]
           [model JsonPailStructure]
           [java.util List]))

(defn- pail-tap
  "Generic pailtap constructor."
  [path colls structure word]
  (let [seqs (into-array List colls)
        spec (PailTap/makeSpec nil structure)
        opts (PailTap$PailTapOptions. spec word seqs nil)]
    (PailTap. path opts)))

(defn json-pail-tap
  "A json pail tap for the pail at given path"
  [path]
  (pail-tap path nil (JsonPailStructure.) "data"))

(defn- ?pail-*
  "Executes supplied query into prexisting pail at pail-path"
  [pail-path query]
  (with-fs-tmp [_ tmp]
    (let [out-tap (json-pail-tap tmp)
          root-pail (Pail. pail-path)]
      (?- out-tap query)
      (.absorb root-pail (Pail. tmp)))))

(defn ?pail-
  "Executes supplied query into json pail at root.  Creates pail if it doesn't already exist."
  [root query]
  (let [_ (get-or-create-json-pail root)]
    (?pail-* root query)))
