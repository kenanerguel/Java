-- Lösche vorhandene Benutzer (falls vorhanden)
DELETE FROM users;

-- Füge Admin-Benutzer hinzu
INSERT INTO users (username, password, is_admin) VALUES ('admin', 'pRnf+TXORKJefRH6x9HcfOyIxKEetJvxAqZc10GgB/bxuzFtjHMtSLqaPMYAU9zelkfUwAaP8S0QnclBkbgVJQ==', b'1');

-- Füge Wissenschaftler hinzu
INSERT INTO users (username, password, is_admin) VALUES ('scientist', 'Rp8KPc67CVZdkW/GdjvnQuFNfaBxRBSnYd8wPw4cQyWE/+Ef60ujGAjp1EoO3M5YdUjvp0YPw2dpurT++YRCsg==', b'0'); 