-- Tabellen löschen, falls sie existieren
DROP TABLE IF EXISTS artikel;
DROP TABLE IF EXISTS users;

-- Benutzer-Tabelle erstellen
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- Artikel-Tabelle erstellen
CREATE TABLE artikel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    land VARCHAR(255) NOT NULL,
    jahr INTEGER NOT NULL,
    co2ausstoss DOUBLE DEFAULT 0.0,
    einheit VARCHAR(255) NOT NULL,
    beschreibung TEXT,
    status VARCHAR(255) NOT NULL,
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(username)
); 