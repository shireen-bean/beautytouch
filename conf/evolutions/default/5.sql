# Brand information table

# --- !Ups

CREATE TABLE `brands` (
  `id` bigint not null auto_increment unique,
  `name` varchar(255),
  `description` text,
  `logo` varchar(45)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `products`
  ADD `brand_id` bigint,
  ADD CONSTRAINT `product_brand_id` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`) ON UPDATE CASCADE;

# --- !Downs

ALTER TABLE `products`
  DROP FOREIGN KEY `product_brand_id`;
ALTER TABLE `products`
  DROP COLUMN `brand_id`;
DROP TABLE `brands`;
