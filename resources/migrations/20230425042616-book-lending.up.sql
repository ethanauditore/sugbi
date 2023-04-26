create table catalog.lending (
  lending_id bigint generated always as identity primary key,
  book_id bigint not null references catalog.book,
  user_id bigint not null unique,
  lending_date date,
  due_date date,
  check(due_date - lending_date <= 14)
);
