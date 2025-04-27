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

FLUSH PRIVILEGES; 