-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'admin123', b'1');

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('scientist', 'scientist123', b'0'); 