-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==', 1);

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('science1', 'hbhYGATKF5tsfqaK7ycsZMsZBMVZNXwwxKQ0/rj373bUlV7e6tIlF7tc4VvD5y47erAJRRrjPtuxTSfHtCMdow==', 0); 