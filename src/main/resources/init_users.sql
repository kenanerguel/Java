-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM user_roles;
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==', TRUE);

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('science1', 'hbhYGATKF5tsfqaK7ycsZMsZBMVZNXwwxKQ0/rj373bUlV7e6tIlF7tc4VvD5y47erAJRRrjPtuxTSfHtCMdow==', FALSE);

-- Assign roles
INSERT INTO user_roles (user_id, role) SELECT id, 'ADMIN' FROM users WHERE username = 'admin';
INSERT INTO user_roles (user_id, role) SELECT id, 'SCIENTIST' FROM users WHERE username = 'science1'; 