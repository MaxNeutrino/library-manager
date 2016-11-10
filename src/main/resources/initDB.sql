DROP TABLE IF EXISTS books;

CREATE TABLE books
(
  name VARCHAR(255) NOT NULL,
  status VARCHAR(255)
);

CREATE UNIQUE INDEX name_book_key ON BOOKS (name);

INSERT INTO books (name, status) VALUES
  ('J. Rowling “Harry Potter”', 'read'),
  ('Unknown “Harry Potter”', 'read'),
  ('Linus Torvalds "Just for fun"', 'wish'),
  ('Bruce Eckel "Thinking in Java"', 'read');