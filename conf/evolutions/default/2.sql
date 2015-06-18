# Sales log schema

# --- !Ups

CREATE TABLE `sales` (
  `id` bigint not null auto_increment unique,
  `machine_id` int(11) NOT NULL,
  `sales_total` float not null,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `sales_machine_id` FOREIGN KEY (`machine_id`) REFERENCES `machines` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

create table `sales_products` (
  `sales_id` bigint not null,
  `product_sku` int(11) not NULL,
  `product_price` float not null,
  constraint `sales_id` FOREIGN KEY (`sales_id`) REFERENCES `sales` (`id`),
  constraint `product_sku` foreign key (`product_sku`) REFERENCES `products` (`itemSku`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `sales_customer` (
  `sales_id` bigint,
  `customer_email` varchar(255),
  `customer_phone` varchar(15),
  constraint `customer_sales_id` FOREIGN KEY (`sales_id`) REFERENCES `sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# --- !Downs

DROP TABLE sales_customer;
DROP TABLE sales_products;
DROP TABLE sales;
