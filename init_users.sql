-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'admin123', true);

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('science1', 'pass123', false);
INSERT INTO users (username, password, is_admin) VALUES ('science2', 'pass456', false); 