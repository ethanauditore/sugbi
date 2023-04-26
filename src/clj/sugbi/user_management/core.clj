(ns sugbi.user-management.core
  (:require
   [sugbi.user-management.db :as user-management.db]))


(defn is-librarian?
  [sub]
  (boolean
   (user-management.db/get-librarian {:sub sub})))
