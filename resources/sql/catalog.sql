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

-- :name get-book-item :? :*
select *
from catalog.book_item
where book_item_id = :book-item-id;
--
-- :name get-book-items-by-isbn :? :*
select *
from catalog.book natural join catalog.book_item
where isbn = :isbn;

-- :name checkout-book! :! :1
insert into catalog.lending (user_id, book_item_id)
values (:user-id, :book-item-id)
returning lending_id, lending_date, due_date, return_date;

-- :name return-book! :! :n
update catalog.lending
set return_date = :return-date
where book_item_id = :book-item-id;

-- :name get-book-lendings-by-user :? :*
select lending_id, lending_date, due_date, return_date
from catalog.lending
where user_id = :user-id;;

-- :name get-book-lendings :? :*
select *
from catalog.lending
where book_item_id = :book-item-id;

-- :name get-book-lendings-by-isbn :? :*
select isbn, lending_id
from catalog.book natural join catalog.book_item natural join catalog.lending
where isbn = :isbn;
