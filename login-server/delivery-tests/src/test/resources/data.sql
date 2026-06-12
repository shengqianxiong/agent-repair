INSERT INTO login_account (account, password, status, role) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1),
('user', '6ad14ba9986e3615423dfca256d04aa3', 1, 0);

INSERT INTO bar_product_category (name, sort, status) VALUES ('啤酒', 1, 1);
INSERT INTO bar_product_category (name, sort, status) VALUES ('洋酒', 2, 1);

INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('青岛纯生', 1, 18.00, 100, 1);
INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('百威啤酒', 1, 22.00, 80, 1);
INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('芝华士12年', 2, 288.00, 20, 1);
INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('下架商品', 1, 10.00, 0, 0);
