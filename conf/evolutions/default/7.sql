# New product Images

# --- !Ups

ALTER TABLE `products`
  ADD `thumbnail` varchar(255),
  ADD `detailImg` varchar(255);


# --- !Downs

ALTER TABLE `products`
  DROP COLUMN `thumbnail`,
  DROP COLUMN `detailImg`;
