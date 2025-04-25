-- Tabellen löschen, falls sie existieren
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS artikel;
DROP TABLE IF EXISTS users;

-- Benutzer-Tabelle erstellen
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- Rollen-Tabelle erstellen
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id)
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
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
); 