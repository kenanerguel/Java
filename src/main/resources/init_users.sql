-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM user_roles;
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', '8sTnqNMpAWKHeFVGslHrmGN1UtRmUXbEDEJnzQXML05QTP4kxQQOLFAMybPuEMp012EGv0uCrgBxGp8dG1RhyA==', TRUE);

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('science1', 'KW/Q+quBB936f+vEzM79JDs+4TYDraef7VS8i/vAS8fj6Zr+fvwOIk28l1G7IP0p1JmEeNvJj+BBdFia6EXKUw==', FALSE);

-- Assign roles
INSERT INTO user_roles (user_id, role) SELECT id, 'ADMIN' FROM users WHERE username = 'admin';
INSERT INTO user_roles (user_id, role) SELECT id, 'SCIENTIST' FROM users WHERE username = 'science1'; 