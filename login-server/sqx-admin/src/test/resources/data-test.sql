-- 测试数据：admin / admin123
INSERT INTO login_account (username, password, status, create_time, update_time)
SELECT 'admin', '0192023a7bbd73250516f069df18b500', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM login_account WHERE username = 'admin');

INSERT INTO login_account (username, password, status, create_time, update_time)
SELECT 'user01', '5f4dcc3b5aa765d61d8327deb882cf99', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM login_account WHERE username = 'user01');
