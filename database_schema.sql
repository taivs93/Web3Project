-- Database Schema cho Web3 Authentication System
-- Tạo database (nếu chưa có)
CREATE DATABASE IF NOT EXISTS web3_auth_db;
USE web3_auth_db;

-- Bảng Wallet để lưu thông tin ví
CREATE TABLE wallet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(42) NOT NULL UNIQUE COMMENT 'Địa chỉ ví Ethereum (42 ký tự bao gồm 0x)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_address (address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng User để lưu thông tin người dùng
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    username VARCHAR(100) UNIQUE COMMENT 'Tên người dùng (có thể null)',
    email VARCHAR(255) UNIQUE COMMENT 'Email (có thể null)',
    avatar_url VARCHAR(500) COMMENT 'URL avatar',
    bio TEXT COMMENT 'Mô tả về người dùng',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái hoạt động',
    last_login_at TIMESTAMP NULL COMMENT 'Thời gian đăng nhập cuối',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallet(id) ON DELETE CASCADE,
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm comment cho bảng
ALTER TABLE wallet COMMENT = 'Bảng lưu thông tin ví Ethereum';
ALTER TABLE user COMMENT = 'Bảng lưu thông tin người dùng liên kết với ví';

-- Tạo view để dễ dàng query thông tin user và wallet
CREATE VIEW user_wallet_view AS
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    u.avatar_url,
    u.bio,
    u.is_active,
    u.last_login_at,
    u.created_at as user_created_at,
    w.id as wallet_id,
    w.address as wallet_address,
    w.created_at as wallet_created_at
FROM user u
JOIN wallet w ON u.wallet_id = w.id;

-- Insert dữ liệu mẫu (tùy chọn)
-- INSERT INTO wallet (address) VALUES ('0x742d35Cc6634C0532925a3b8D4C9db96C4b4d8b6');
-- INSERT INTO user (wallet_id, username, email, bio) VALUES (1, 'testuser', 'test@example.com', 'Test user bio');
