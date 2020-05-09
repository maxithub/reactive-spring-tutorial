create table book (
    isbn varchar(20) primary key,
    title varchar(500) not null,
    price decimal(10,2) not null,
    category varchar(50) not null
);