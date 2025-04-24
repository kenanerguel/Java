-- Tabellen löschen, falls sie existieren
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS artikel;
DROP TABLE IF EXISTS users;

-- Benutzer-Tabelle erstellen
CREATE TABLE users (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

-- Rollen-Tabelle erstellen
CREATE TABLE user_roles (
    username VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (username, role),
    FOREIGN KEY (username) REFERENCES users(username)
);

-- Artikel-Tabelle erstellen
CREATE TABLE artikel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    land VARCHAR(100) NOT NULL,
    jahr INTEGER NOT NULL,
    co2_ausstoss DOUBLE NOT NULL,
    einheit VARCHAR(10) NOT NULL,
    beschreibung TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    username VARCHAR(50),
    erstellt_am TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username)
); 