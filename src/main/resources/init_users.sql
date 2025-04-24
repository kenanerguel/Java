-- Benutzer und Rollen löschen
DELETE FROM user_roles;
DELETE FROM users;

-- Admin-Benutzer anlegen (Passwort: 'admin')
INSERT INTO users (username, password) VALUES ('admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=');
INSERT INTO user_roles (username, role) VALUES ('admin', 'ADMIN');

-- Wissenschaftler anlegen (Passwort: 'scientist')
INSERT INTO users (username, password) VALUES ('Doktor Niklas', 'Fe26+nqJqKRhxG7G7FBlzKb8H14dR5X6B6Vz3zcvGhI=');
INSERT INTO user_roles (username, role) VALUES ('Doktor Niklas', 'SCIENTIST');

INSERT INTO users (username, password) VALUES ('Nina Maler', 'Fe26+nqJqKRhxG7G7FBlzKb8H14dR5X6B6Vz3zcvGhI=');
INSERT INTO user_roles (username, role) VALUES ('Nina Maler', 'SCIENTIST'); 