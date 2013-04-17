(ns processor.core
  (:use [cascalog.api])
  (:require [cascalog.ops :as c]
            [processor.tap :as t]
            [processor.source :as s])
  (:import [backtype.hadoop.pail Pail]))

(def users   (s/users))
(def tweets  (s/tweets))
(def follows (s/follows))

;; simple

(defn people
  []
  (?<- (stdout)
       [?user]
       ((s/users) ?user)))

(defn people2
  []
  (?- (stdout)
      (s/users)))

(defn follows
  []
  (?<- (stdout)
       [?user1 ?user2]
       ((s/follows) ?user1 ?user2)))

(defn follows-data
  []
  (?<- (stdout)
       [?user1]
       ((s/follows) ?user1 "dhh")
       (:distinct true)))

(defn dhh-tweets
  []
  (?<- (stdout)
       [?text]
       ((s/tweets) "dhh" ?text)))

;; with aggregation

(defn get-hashtags*
  "Extract hashtags from a string"
  [text]
  (map last (re-seq #"(?:^|\s)(#[^\s,\.]+)" text)))

(defmapcatop get-hashtags [text] (get-hashtags* text))

(defn hashtags
  []
  (?<- (stdout)
       [?user ?hashtag]
       ((s/users) ?user)
       ((s/tweets) ?user ?text)
       (= ?user "mudge")
       (get-hashtags ?text :> ?hashtag)))

(defn follow-hashtags
  []
  (?<- (stdout)
       [?user ?friend ?hashtag]
       ((s/users) ?user)
       ((s/follows) ?user ?friend)
       (= ?user "scrundle")
       ((s/tweets) ?friend ?text)
       (get-hashtags ?text :> ?hashtag)))

(defn follow-hashtags-with-counts
  []
  (<-  [?user ?hashtag ?count]
       ((s/users) ?user)
       ((s/follows) ?user ?friend)
       (= ?user "scrundle")
       ((s/tweets) ?friend ?text)
       (c/count ?count)
       (get-hashtags ?text :> ?hashtag)))

(defn top-hashtags-with-counts
  []
  (<- [?user ?hashtag ?count]
      ((follow-hashtags-with-counts) ?user ?hashtag-all ?count-all)
      (:sort ?count-all)
      (:reverse true)
      (c/limit [5] ?hashtag-all ?count-all :> ?hashtag ?count)))

(defmapop mk-output
  [user hashtag count]
  {:user user
   :hashtag hashtag
   :count count})

(defn query
  []
  (<- [?output]
      ((top-hashtags-with-counts) ?user ?hashtag ?count)
      (mk-output ?user ?hashtag ?count :> ?output)))


