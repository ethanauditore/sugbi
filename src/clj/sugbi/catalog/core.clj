(ns sugbi.catalog.core
 (:require
  [clojure.set :as set]
  [sugbi.catalog.db :as db]
  [sugbi.catalog.open-library-books :as olb]))


(defn merge-on-key
  [k x y]
  (->> (concat x y)
       (group-by k)
       (map val)
       (mapv (fn [[x y]] (merge x y)))))


(def available-fields olb/relevant-fields)


(defn get-book
  [isbn fields]
  (when-let [db-book (db/get-book {:isbn isbn})]
    (let [open-library-book-info (olb/book-info isbn fields)
          updated-db-book        (db/update-book-availablity db-book)]
      (merge updated-db-book open-library-book-info))))

(defn get-books
  [fields]
  (let [db-books                (db/get-books {})
        isbns                   (map :isbn db-books)
        open-library-book-infos (olb/multiple-book-info isbns fields)
        updated-db-books        (map db/update-book-availablity db-books)]
    (merge-on-key
     :isbn
     updated-db-books
     open-library-book-infos)))

(defn enriched-search-books-by-title
  [title fields]
  (let [db-book-infos           (db/matching-books title)
        isbns                   (map :isbn db-book-infos)
        open-library-book-infos (olb/multiple-book-info isbns fields)
        updated-db-book-infos   (map db/update-book-availablity db-book-infos)]
    (merge-on-key
     :isbn
     updated-db-book-infos
     open-library-book-infos)))

(defn book-item-id-match-isbn?
  [book-item-id book-item-by-isbn]
  (some true? (mapv #(some-> % :book-item-id (= book-item-id)) book-item-by-isbn)))

(defn is-borrowed?
  [book-item-id]
  (let [book-lendings (db/book-lendings book-item-id)]
    (some #(nil? (:return-date %)) book-lendings)))

(defn is-borrowed-by-user?
  [book-item-id user-id]
  (let [book-lendings (db/book-lendings book-item-id)]
    (= user-id (:user-id (first (filterv #(nil? (:return-date %)) book-lendings))))))
