-- :name insert-book! :! :1
insert into catalog.book (title, isbn) values (:title, :isbn)
returning *;

-- :name delete-book! :! :n
delete from catalog.book where isbn = :isbn;

-- :name search :? :*
select isbn, copies, true as "available"
from catalog.book
where lower(title) like :title;

-- :name get-book :? :1
select isbn, copies, true as "available"
from catalog.book
where isbn = :isbn

-- :name get-books :? :*
select isbn, copies, true as "available"
from catalog.book;

-- :name add-book-item! :! :1
insert into catalog.book_item (book_id)
values (:book-id) returning *;

-- :name checkout-book! :! :1
insert into catalog.lending (user_id, book_item_id)
values (:user-id, :book-item-id) returning *;

-- :name return-book! :! :n
delete from catalog.lending
where user_id = :user-id and book_item_id = :book-item-id;

-- :name get-book-lendings :? :*
select book_item_id, lending_date, due_date
from catalog.lending
where user_id = :user-id;

-- :name get-book-lendings-by-isbn :? :*
select isbn, lending_id
from catalog.book natural join catalog.book_item natural join catalog.lending
where isbn = :isbn;
