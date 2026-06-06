-- 测试环境初始化账号：admin/admin123（MD5）
INSERT INTO login_account (username, password, status, role, create_time, update_time) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user01', '0192023a7bbd73250516f069df18b500', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
