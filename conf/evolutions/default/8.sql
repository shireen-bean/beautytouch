# ORM Integration

# --- !Ups

alter table sales_products drop foreign key product_sku;
alter table events drop foreign key event_product_sku;
alter table products DROP PRIMARY KEY, change itemSku item_sku int(5) not null;
alter table products change item_sku item_sku int not null auto_increment, add primary key (item_sku);
alter table products change itemName item_name VARCHAR(255);
alter table products change itemImg item_img VARCHAR(255);
alter table products change detailImg detail_img VARCHAR(255);
alter table products change itemDescription item_description TEXT;
alter table products change packageType package_type VARCHAR(45);
alter table containers change totalCapacity total_capacity INT;
alter table machines add column total_capacity INT;
alter table machines add column num_items INT;
alter table containers change numItems num_items INT;
alter table containers change itemSku item_sku INT;
alter table containers change machineId machine_id INT;
alter table sales_products add constraint `product_sku` foreign key (`product_sku`) REFERENCES `products` (`item_sku`) ON UPDATE CASCADE;
alter table events add constraint `event_product_sku` foreign key (`product_sku`) references `products` (`item_sku`) on update cascade;

# --- !Downs

#TODO
