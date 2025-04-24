-- Benutzer und Rollen löschen
DELETE FROM user_roles;
DELETE FROM users;

-- Admin-Benutzer anlegen
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$PrI5Gk9L.tTZNXT9TK7Qo.V2V/SZ7G7tV3hSHOKh.D9VIUff3YHJm');
INSERT INTO user_roles (username, role) VALUES ('admin', 'ADMIN');

-- Wissenschaftler anlegen
INSERT INTO users (username, password) VALUES ('Doktor Niklas', '$2a$10$PrI5Gk9L.tTZNXT9TK7Qo.V2V/SZ7G7tV3hSHOKh.D9VIUff3YHJm');
INSERT INTO user_roles (username, role) VALUES ('Doktor Niklas', 'SCIENTIST');

INSERT INTO users (username, password) VALUES ('Nina Maler', '$2a$10$PrI5Gk9L.tTZNXT9TK7Qo.V2V/SZ7G7tV3hSHOKh.D9VIUff3YHJm');
INSERT INTO user_roles (username, role) VALUES ('Nina Maler', 'SCIENTIST'); 