-- :name insert-librarian! :! :1
insert into user_management.librarian (sub) values (:sub)
returning *;

-- :name get-librarian :? :1
select true
from user_management.librarian
where sub = :sub;
