CREATE TABLE account (
    id VARCHAR(50) PRIMARY KEY,
    daily_limit DECIMAL(15,2) NOT NULL,
    accumulated_daily DECIMAL(15,2) DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_reference VARCHAR(100) NOT NULL UNIQUE,
    source_account_id VARCHAR(50) NOT NULL,
    destination_account_id VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);