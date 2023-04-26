(ns sugbi.user-management.db
  (:require
   [conman.core :as conman]
   [sugbi.db.core :as db]))


(conman/bind-connection db/*db* "sql/user-management.sql")
