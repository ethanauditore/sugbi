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
