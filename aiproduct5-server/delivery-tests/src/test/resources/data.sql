INSERT INTO login_account (account, password, status, role) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1),
('user', '6ad14ba9986e3615423dfca256d04aa3', 1, 0);

INSERT INTO bar_product_category (name, sort, status) VALUES ('啤酒', 1, 1);
INSERT INTO bar_product_category (name, sort, status) VALUES ('洋酒', 2, 1);

INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('青岛纯生', 1, 18.00, 100, 1);
INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('百威啤酒', 1, 22.00, 80, 1);
INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('芝华士12年', 2, 288.00, 20, 1);
INSERT INTO bar_product (name, category_id, price, stock, status) VALUES ('下架商品', 1, 10.00, 0, 0);

INSERT INTO review_activity (id, code, title, product_name, product_image, shop_name, rebate_amount, valid_end_time, douyin_link, copywriting_json, recommend_images_json, status, scan_count, submit_count, verify_count) VALUES
(10001, 'DP20260611', '满杯鲜萃茶饮评价任务', '招牌厚乳拿铁',
 'https://images.unsplash.com/photo-1541167760496-1628856ab772?auto=format&fit=crop&w=520&q=80',
 '豆评严选旗舰店', 20.00, TIMESTAMP '2026-06-30 23:59:00', 'https://www.douyin.com',
 '[{"id":1,"title":"真实体验版","content":"这杯厚乳拿铁口感很顺，奶香和咖啡香平衡得刚好，包装也很干净。到手后冰感还在，整体体验超出预期，适合下午提神。"},{"id":2,"title":"精简好评版","content":"口感顺滑，奶香浓郁，甜度刚好，配送速度也快。已经收藏店铺，下次还会回购。"},{"id":3,"title":"图文种草版","content":"颜值和口味都在线，杯身很适合拍照。入口不会腻，厚乳香气明显，喜欢奶咖的朋友可以放心冲。"}]',
 '["https://images.unsplash.com/photo-1522992319-0365e5f11656?auto=format&fit=crop&w=520&q=80","https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&w=520&q=80"]',
 1, 12, 3, 1);

INSERT INTO review_task (id, activity_id, user_identifier, status, screenshot_url, create_time) VALUES
(88001, 10001, 'visitor_demo_001', 'auditing', 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=520&q=80', TIMESTAMP '2026-06-11 14:28:00');

INSERT INTO review_rebate (id, task_id, activity_id, user_identifier, amount, status, create_time) VALUES
(66001, 88001, 10001, 'visitor_demo_001', 20.00, 'auditing', TIMESTAMP '2026-06-11 14:28:00');
