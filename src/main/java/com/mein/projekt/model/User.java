package com.mein.projekt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // "user" kann ein reserviertes Schlüsselwort sein – daher besser "users"
public class User {

    // Hier wird der Benutzername als Primärschlüssel genutzt.
    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    // Standard-Konstruktor (erforderlich für JPA)
    public User() {
    }

    // Konstruktor zur einfachen Initialisierung
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getter und Setter
    public String getUsername() {
        return username;
    }
}
