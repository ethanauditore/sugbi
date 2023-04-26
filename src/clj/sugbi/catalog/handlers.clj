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
