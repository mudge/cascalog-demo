(ns processor.source
  (:use [cascalog.api]
        [processor.tap :only [json-pail-tap]])
   (:require [clojure.string :as s]
             [cheshire.core :as json]
             [cascalog.ops :as c]))

(def pail-root "/Users/ben/github/benmiles/cascalog-demo/pails")

(defn users
  "A cascalog source for [?user]"
  []
  (let [tap (json-pail-tap (str pail-root "/users"))]
    (<- [?user]
        (tap ?_ ?full-user)
        (get-in ?full-user [:id] :> ?user))))

(defn follows
  "A cascalog source for [?user1 ?user2] where user1 follows user2"
  []
  (let [tap (json-pail-tap (str pail-root "/follows"))]
    (<- [?user1 ?user2]
        (tap ?_ ?follows)
        (get-in ?follows [:user_id] :> ?user1)
        (get-in ?follows [:target_id] :> ?user2))))

(defn tweets
  "A cascalog source for [?user ?text]"
  []
  (let [tap (json-pail-tap (str pail-root "/tweets"))]
    (<- [?user ?text]
        (tap ?_ ?tweet)
        (get-in ?tweet [:id] :> ?id)
        (get-in ?tweet [:user_id] :> ?user)
        (get-in ?tweet [:text] :> ?text))))
