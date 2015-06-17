# Event log schema

# --- !Ups

CREATE TABLE `events` (
  `id` bigint not null auto_increment unique,
  `machine_id` int(11) NOT NULL,
  `event` varchar(255) NOT NULL,
  `product_sku` int(11) default null,
  `time` timestamp not null default current_timestamp,
  constraint `event_machine_id` foreign key (`machine_id`) references `machines` (`id`) on update cascade,
  constraint `event_product_sku` foreign key (`product_sku`) references `products` (`itemSku`) on update cascade
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# --- !Downs

DROP TABLE events;
