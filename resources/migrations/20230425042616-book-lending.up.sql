create table catalog.book_item (
  book_item_id bigint generated always as identity primary key,
  book_id bigint not null references catalog.book
);
--;;
create table catalog.lending (
  lending_id bigint generated always as identity primary key,
  book_item_id bigint not null unique references catalog.book_item,
  user_id bigint not null,
  lending_date date not null default current_date,
  due_date date not null default current_date + 14,
  check(due_date - lending_date <= 14)
);
