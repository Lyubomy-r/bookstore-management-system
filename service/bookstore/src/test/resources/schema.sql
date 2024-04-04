create schema  if not exists mydb;

create table if not exists mydb.book
(
    id       uuid  not null
        primary key,
    title    varchar(255),
    author   varchar(255),
    isbn     varchar(20),
    quantity integer
);
