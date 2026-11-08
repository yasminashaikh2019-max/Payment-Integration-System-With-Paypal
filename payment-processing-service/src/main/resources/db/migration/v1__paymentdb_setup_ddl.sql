-- Deletes the payments database only if it already exists.

DROP DATABASE IF EXISTS payments;

-- Deletes the MySQL user payments_user if the user already exists.

DROP USER IF EXISTS 'payments_user'@'%';

-- Creates a new database named payments

CREATE DATABASE payments;

-- Creates a new MySQL user with the given username and password. % - user can connect from any host/IP.

CREATE USER 'payments_user'@'%' IDENTIFIED BY 'P9v@tX3#nLz!Q8wK';

-- GRANT permission on all tables inside payments database.

GRANT SELECT, INSERT, UPDATE, DELETE ON payments.* TO 'payments_user'@'%';

-- GRANT END Either this.

-- Create Tables payments Schema Start***

CREATE TABLE payments.`payment_method` (
    `id` int NOT NULL,
    `name` varchar(50) NOT NULL,
    `status` tinyint DEFAULT 1,
    `created_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    `updated_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2) ON UPDATE CURRENT_TIMESTAMP(2),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`payment_type` (
    `id` int NOT NULL,
    `type` varchar(50) NOT NULL,
    `status` tinyint DEFAULT 1,
    `created_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    `updated_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2) ON UPDATE CURRENT_TIMESTAMP(2),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`provider` (
    `id` int NOT NULL AUTO_INCREMENT,
    `provider_name` varchar(50) NOT NULL,
    `status` tinyint DEFAULT 1,
    `created_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    `updated_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2) ON UPDATE CURRENT_TIMESTAMP(2),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`transaction_status` (
    `id` int NOT NULL,
    `name` varchar(50) NOT NULL,
    `status` tinyint DEFAULT 1,
    `created_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    `updated_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2) ON UPDATE CURRENT_TIMESTAMP(2),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`transaction` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` varchar(50) NOT NULL,
    `payment_method_id` int NOT NULL,
    `provider_id` int NOT NULL,
    `payment_type_id` int NOT NULL,
    `txn_status_id` int NOT NULL,
    `amount` decimal(19,2) DEFAULT '0.00',
    `currency` varchar(3) NOT NULL,
    `merchant_transaction_reference` varchar(50) NOT NULL,
    `txn_reference` varchar(50) NOT NULL,
    `provider_reference` varchar(100) DEFAULT NULL,
    `error_code` varchar(500) DEFAULT NULL,
    `error_message` varchar(1000) DEFAULT NULL,
    `created_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    `updated_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2) ON UPDATE CURRENT_TIMESTAMP(2),
    `retry_count` int DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_txn_reference` (`txn_reference`),
    KEY `idx_transaction_payment_method_id` (`payment_method_id`),
    KEY `idx_transaction_provider_id` (`provider_id`),
    KEY `idx_transaction_txn_status_id` (`txn_status_id`),
    KEY `idx_transaction_payment_type_id` (`payment_type_id`),
    CONSTRAINT `fk_transaction_payment_method_id` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`),
    CONSTRAINT `fk_transaction_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`),
    CONSTRAINT `fk_transaction_txn_status_id` FOREIGN KEY (`txn_status_id`) REFERENCES `transaction_status` (`id`),
    CONSTRAINT `fk_transaction_payment_type_id` FOREIGN KEY (`payment_type_id`) REFERENCES `payment_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments.`transaction_log` (
    `id` int NOT NULL AUTO_INCREMENT,
    `transaction_id` int NOT NULL,
    `txn_from_status` varchar(50) DEFAULT '-1',
    `txn_to_status` varchar(50) DEFAULT '-1',
    `created_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    `updated_at` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2) ON UPDATE CURRENT_TIMESTAMP(2),
    PRIMARY KEY (`id`),
    KEY `idx_transaction_log_transaction_id` (`transaction_id`),
    CONSTRAINT `fk_transaction_log_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Tables payments Schema End***