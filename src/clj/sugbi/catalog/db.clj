(ns sugbi.catalog.db
 (:require
  [camel-snake-kebab.core :as csk]
  [clojure.string :as str]
  [conman.core :as conman]
  [sugbi.db.core :as db]
  [medley.core :as medley]))

(conman/bind-connection db/*db* "sql/catalog.sql")


(defn matching-books
  [title]
  (map
   #(medley/map-keys csk/->kebab-case %)
   (search {:title (str "%" (str/lower-case title) "%")})))

(defn update-book-availablity
  [book]
  (letfn [(number-of-lendings
            [isbn]
            (count (get-book-lendings-by-isbn {:isbn isbn})))]
    (let [isbn   (:isbn book)
          copies (:copies book)]
      (assoc book :available (< (number-of-lendings isbn) copies)))))
