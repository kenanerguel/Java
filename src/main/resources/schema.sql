-- Create users table if not exists
CREATE TABLE IF NOT EXISTS User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin TINYINT(1) NOT NULL DEFAULT 0
);

-- Create articles table if not exists
CREATE TABLE IF NOT EXISTS Artikel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    land VARCHAR(255) NOT NULL,
    jahr INT NOT NULL,
    co2ausstoss DOUBLE,
    einheit VARCHAR(50) DEFAULT 'Tonnen',
    beschreibung TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    user_id BIGINT,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Create countries table if not exists
CREATE TABLE IF NOT EXISTS Country (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(2) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin user if not exists (password: admin)
INSERT INTO User (username, password, is_admin) 
VALUES ('admin', '$2a$10$qPOYBWn9dFAuLZvLhBWqEeZqZKnGGXizbanYxcpYxGVr0yZxvMhGi', 1)
ON DUPLICATE KEY UPDATE password='$2a$10$qPOYBWn9dFAuLZvLhBWqEeZqZKnGGXizbanYxcpYxGVr0yZxvMhGi';

-- Insert some default countries if not exists
INSERT INTO Country (name, code) VALUES
('Germany', 'DE'),
('United States', 'US'),
('United Kingdom', 'GB'),
('France', 'FR'),
('Italy', 'IT')
ON DUPLICATE KEY UPDATE id=id; 