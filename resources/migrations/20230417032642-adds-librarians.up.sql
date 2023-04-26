create schema user_management;
--;;
create table user_management.librarian (
  librarian_id bigint generated always as identity primary key,
  sub text not null unique
);
