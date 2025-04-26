-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==', 1);

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('science1', 'KW/Q+quBB936f+vEzM79JDs+4TYDraef7VS8i/vAS8fj6Zr+fvwOIk28l1G7IP0p1JmEeNvJj+BBdFia6EXKUw==', 0); 