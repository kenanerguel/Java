-- Tabellen löschen falls sie existieren
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS artikel;

-- Tabellen erstellen
CREATE TABLE users (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    username VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE artikel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    land VARCHAR(100) NOT NULL,
    jahr INT NOT NULL,
    co2_ausstoss DECIMAL(10,2) NOT NULL,
    einheit VARCHAR(20) NOT NULL,
    beschreibung TEXT,
    erstellt_am DATE,
    wissenschaftler VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending'
);

-- Admin und Wissenschaftler anlegen
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$PrI5Gk9L.tTZNXT9TK7Qo.V2V/SZ7G7tV3hSHOKh.D9VIUff3YHJm');
INSERT INTO user_roles (username, role) VALUES ('admin', 'ADMIN');

INSERT INTO users (username, password) VALUES ('Doktor Niklas', '$2a$10$PrI5Gk9L.tTZNXT9TK7Qo.V2V/SZ7G7tV3hSHOKh.D9VIUff3YHJm');
INSERT INTO user_roles (username, role) VALUES ('Doktor Niklas', 'SCIENTIST');

INSERT INTO users (username, password) VALUES ('Nina Maler', '$2a$10$PrI5Gk9L.tTZNXT9TK7Qo.V2V/SZ7G7tV3hSHOKh.D9VIUff3YHJm');
INSERT INTO user_roles (username, role) VALUES ('Nina Maler', 'SCIENTIST');

-- Beispiel-Länder mit CO2-Daten
INSERT INTO artikel (land, jahr, co2_ausstoss, einheit, beschreibung, erstellt_am, wissenschaftler, status) VALUES
('Deutschland', 2023, 15.2, 'Tonnen', 'Wissenschaftliche Erhebung', CURRENT_DATE, 'Doktor Niklas', 'approved'),
('Frankreich', 2023, 12.8, 'Tonnen', 'Offizielle Messungen', CURRENT_DATE, 'Nina Maler', 'approved'),
('USA', 2023, 18.5, 'Tonnen', 'EPA Daten', CURRENT_DATE, 'Doktor Niklas', 'approved'),
('China', 2023, 20.1, 'Tonnen', 'Nationale Statistik', CURRENT_DATE, 'Nina Maler', 'approved'),
('Japan', 2023, 14.3, 'Tonnen', 'Umweltministerium', CURRENT_DATE, 'Doktor Niklas', 'approved'); 