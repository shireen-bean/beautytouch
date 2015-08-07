# Product subtitle

# --- !Ups

ALTER TABLE `products`
  ADD `subtitle` varchar(255);


# --- !Downs

ALTER TABLE `products`
  DROP COLUMN `subtitle`;
