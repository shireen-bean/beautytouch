# Add product category

# --- !Ups

ALTER TABLE products
  ADD category varchar(255);


# --- !Downs

ALTER TABLE products
  DROP COLUMN category;
