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

-- Insert default admin user if not exists (password: admin)
INSERT INTO users (username, password, is_admin) 
VALUES ('admin', '$2a$10$qPOYBWn9dFAuLZvLhBWqEeZqZKnGGXizbanYxcpYxGVr0yZxvMhGi', 1)
ON DUPLICATE KEY UPDATE password='$2a$10$qPOYBWn9dFAuLZvLhBWqEeZqZKnGGXizbanYxcpYxGVr0yZxvMhGi';

-- Insert some default countries if not exists
INSERT INTO countries (name, code) VALUES
('Germany', 'DE'),
('United States', 'US'),
('United Kingdom', 'GB'),
('France', 'FR'),
('Italy', 'IT')
ON DUPLICATE KEY UPDATE id=id; 