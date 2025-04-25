-- Create users table if not exists
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin TINYINT(1) NOT NULL DEFAULT 0
);

-- Create articles table if not exists
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- Create countries table if not exists
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(2) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin user if not exists
INSERT INTO users (username, password, is_admin) 
VALUES ('admin', '$2a$10$X7G3Y5J9Z1B2C4D6E8F0G1H2I3J4K5L6M7N8O9P0Q1R2S3T4U5V6W7X8Y9Z', 1)
ON DUPLICATE KEY UPDATE id=id;

-- Insert some default countries if not exists
INSERT INTO countries (name, code) VALUES
('Germany', 'DE'),
('United States', 'US'),
('United Kingdom', 'GB'),
('France', 'FR'),
('Italy', 'IT')
ON DUPLICATE KEY UPDATE id=id; 