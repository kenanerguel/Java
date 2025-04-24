-- Benutzer und Rollen löschen
DELETE FROM user_roles;
DELETE FROM users;

-- Insert admin user (password: admin)
INSERT INTO users (username, password) VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918');

-- Insert scientist user (password: scientist)
INSERT INTO users (username, password) VALUES ('scientist', '7b52009b64fd0a2a49e6d8a939753077792b0554c8a7c79f0e8c02c856ee15f5');

-- Assign roles
INSERT INTO user_roles (user_id, role) SELECT id, 'ADMIN' FROM users WHERE username = 'admin';
INSERT INTO user_roles (user_id, role) SELECT id, 'SCIENTIST' FROM users WHERE username = 'scientist'; 