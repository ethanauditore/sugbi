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

;; (insert-book! {:title "Misery" :isbn "1501156748"})
;; (insert-book! {:title "The Shining" :isbn "0345806786"})

;; (add-book-item! {:book-id 1})
;; (add-book-item! {:book-id 2})

;; (checkout-book! {:user-id 1 :book-item-id 1})
;; (checkout-book! {:user-id 2 :book-item-id 2})
;; (checkout-book! {:user-id 3 :book-item-id 3})
;; (checkout-book! {:user-id 4 :book-item-id 4})
;; (checkout-book! {:user-id 5 :book-item-id 5})

;; (checkout-book! {:user-id 1 :book-item-id 6})
;; (checkout-book! {:user-id 2 :book-item-id 7})

;; (return-book! {:user-id 5 :book-item-id 5})
