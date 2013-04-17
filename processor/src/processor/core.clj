(ns processor.core
  (:use [cascalog.api])
  (:require [cascalog.ops :as c]
            [processor.source :as s])
  (:import [backtype.hadoop.pail Pail]))

;; s/users   [?user]
;; s/follows [?user1 ?user2]
;; s/tweets  [?user ?text]
