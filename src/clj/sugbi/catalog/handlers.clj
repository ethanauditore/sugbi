(ns sugbi.catalog.handlers
  (:require
   [ring.util.http-response :as response]
   [sugbi.catalog.db :as catalog.db]
   [sugbi.catalog.core :as catalog.core]))


(defn search-books
  [request]
  (if-let [criteria (get-in request [:parameters :query :q])]
    (response/ok
     (catalog.core/enriched-search-books-by-title
      criteria
      catalog.core/available-fields))
    (response/ok
     (catalog.core/get-books
      catalog.core/available-fields))))


(defn insert-book!
  [request]
  (let [{:keys [_isbn _title]
         :as book-info} (get-in request [:parameters :body])
        is-librarian?   (get-in request [:session :is-librarian?])]
    (if is-librarian?
      (response/ok
       (select-keys (catalog.db/insert-book! book-info) [:isbn :title]))
      (response/forbidden {:message "Operation restricted to librarians"}))))


(defn delete-book!
  [request]
  (let [isbn          (get-in request [:parameters :path :isbn])
        is-librarian? (get-in request [:session :is-librarian?])]
    (if is-librarian?
      (response/ok
       {:deleted (catalog.db/delete-book! {:isbn isbn})})
      (response/forbidden {:message "Operation restricted to librarians"}))))


(defn get-book
  [request]
  (let [isbn (get-in request [:parameters :path :isbn])]
    (if-let [book-info (catalog.core/get-book
                        isbn
                        catalog.core/available-fields)]
      (response/ok book-info)
      (response/not-found {:isbn isbn}))))

(defn handle-lending
  [request action]
  (if-let [user-id (get-in request [:session :sub])]
    (let [book-item-id            (get-in request [:parameters :path :book-item-id])
          book-item               (catalog.db/get-book-item {:book-item-id book-item-id})
          isbn                    (get-in request [:parameters :path :isbn])
          book-item-by-isbn       (catalog.db/book-items-by-isbn isbn)
          book-item-id-match-isbn (catalog.core/book-item-id-match-isbn?
                                   book-item-id book-item-by-isbn)]
      (if (empty? book-item-by-isbn)
        (response/not-found {:message "Book has no copies"})
        (if (empty? book-item)
          (response/not-found {:message "Book item not found"})
          (if book-item-id-match-isbn
            (action book-item-id user-id)
            (response/conflict {:message "Book item doesn't match isbn"})))))
    (response/forbidden {:message "You need an open session"})))

(defn checkout-lending!
  [request]
  (letfn [(action [book-item-id user-id]
            (if (catalog.core/is-borrowed? book-item-id)
              (response/conflict {:message "The book is already borrowed"})
              (response/ok (catalog.db/create-lending user-id book-item-id))))]
    (handle-lending request action)))

(defn return-book!
  [request]
  (letfn [(action [book-item-id user-id]
            (if (catalog.core/is-borrowed-by-user? book-item-id user-id)
              (response/ok {:updated (catalog.db/return-book!
                                      {:return-date (java.time.LocalDate/now)
                                       :book-item-id book-item-id})})
              (response/conflict {:message "You didn't borrowed this book"})))]
    (handle-lending request action)))

(defn get-book-lendings
  [request]
  (if-let [custom-user-id (get-in request [:parameters :query :user-id])]
    (let [is-librarian? (get-in request [:session :is-librarian?])]
      (if (some? is-librarian?)
        (if (or is-librarian? (= custom-user-id (get-in request [:session :sub])))
          (let [loan-info (catalog.db/book-lendings-by-user custom-user-id)]
            (if (empty? loan-info)
              (response/not-found {:message "User or user loans not found"})
              (response/ok loan-info)))
          (response/forbidden {:message "Operation restricted to librarians"}))
        (response/forbidden {:message "You need an open session"})))
    (if-let [user-id (get-in request [:session :sub])]
      (let [loan-info (catalog.db/book-lendings-by-user user-id)]
        (if (empty? loan-info)
          (response/not-found {:message "You don't have any loans"})
          (response/ok loan-info)))
      (response/forbidden {:message "You need an open session"}))))
