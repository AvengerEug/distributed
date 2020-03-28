CREATE DATABASE goods;

USE goods;


CREATE TABLE goods(
  goods_id INT PRIMARY KEY AUTO_INCREMENT,
  goods_name VARCHAR(255),
  goods_stock INT
);

INSERT INTO goods(goods_name, goods_stock) VALUES("西瓜", 10);