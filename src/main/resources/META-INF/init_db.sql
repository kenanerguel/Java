-- Root-Benutzer Berechtigungen neu setzen
ALTER USER 'root'@'%' IDENTIFIED BY 'MyNewPass123!@#';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

-- co2user erstellen und Berechtigungen setzen
CREATE USER IF NOT EXISTS 'co2user'@'%' IDENTIFIED BY '123';
GRANT ALL PRIVILEGES ON co2db.* TO 'co2user'@'%';

-- Lokalen co2user erstellen
CREATE USER IF NOT EXISTS 'co2user'@'localhost' IDENTIFIED BY '123';
GRANT ALL PRIVILEGES ON co2db.* TO 'co2user'@'localhost';

-- Datenbank erstellen, falls nicht vorhanden
CREATE DATABASE IF NOT EXISTS co2db;

-- Benutzer-Tabelle erstellen
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- Admin-Benutzer erstellen
INSERT INTO users (username, password, is_admin) VALUES 
('admin', 'pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==', true);

-- Wissenschaftler-Benutzer erstellen
INSERT INTO users (username, password, is_admin) VALUES 
('science1', 'hbhYGATKF5tsfqaK7ycsZMsZBMVZNXwwxKQ0/rj373bUlV7e6tIlF7tc4VvD5y47erAJRRrjPtuxTSfHtCMdow==', false);

FLUSH PRIVILEGES; 