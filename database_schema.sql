CREATE DATABASE web3_db;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  wallet_id bigint(20) NOT NULL UNIQUE,
  username varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  email varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  avatar_uri varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  bio text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  is_active tinyint(1) DEFAULT '1',
  last_login_at timestamp NULL DEFAULT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id`),
  UNIQUE KEY username (`username`),
  UNIQUE KEY email (`email`),
  KEY wallet_id (`wallet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
DROP TABLE IF EXISTS wallets;
CREATE TABLE wallets (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  address varchar(42) COLLATE utf8mb4_unicode_ci NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id`),
  UNIQUE KEY address (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE users
  ADD CONSTRAINT users_ibfk_1 FOREIGN KEY (`wallet_id`) REFERENCES wallets (`id`);

ALTER TABLE wallets
    ADD COLUMN nonce varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL;