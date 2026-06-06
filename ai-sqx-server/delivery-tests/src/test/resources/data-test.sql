INSERT INTO bar_product_category (name, sort, status) VALUES
('啤酒', 1, 1),
('鸡尾酒', 2, 1);

INSERT INTO bar_product (name, category_id, image, price, original_price, description, stock, status) VALUES
('青岛纯生', 1, 'https://example.com/beer1.jpg', 18.00, 22.00, '清爽麦香', 200, 1),
('莫吉托', 2, 'https://example.com/mojito.jpg', 48.00, 58.00, '经典鸡尾酒', 80, 1);
