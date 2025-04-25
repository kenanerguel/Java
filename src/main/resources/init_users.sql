-- Benutzer und Rollen löschen
DELETE FROM user_roles;
DELETE FROM users;

-- Füge Admin-Benutzer hinzu (Passwort: admin123)
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'dXRcRLCMz+kUxl9QORmRxyPfliMK/6hF1zild9sVmuu4BHCemIAfqAH8GXjbomZAFmjdAJ0F6nESJhDjCraIRQ==', TRUE);

-- Füge Wissenschaftler hinzu (Passwort: pass123)
INSERT INTO users (username, password, is_admin) VALUES ('science1', '+sasj0x49+vlfzERhRBCRHazOgEOo2WWljaclLTWv9M7xdYJvX0g0hAxFWhErpEebMCQox83NijXNi4AoC3Jhw==', FALSE);

-- Assign roles
INSERT INTO user_roles (user_id, role) SELECT id, 'ADMIN' FROM users WHERE username = 'admin';
INSERT INTO user_roles (user_id, role) SELECT id, 'SCIENTIST' FROM users WHERE username = 'science1'; 