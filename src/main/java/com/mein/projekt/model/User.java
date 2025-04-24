package com.mein.projekt.model;

import com.mein.projekt.dao.ArtikelDAO;
import com.mein.projekt.dao.UserDAO;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.Serializable;

@Entity
@Table(name = "users") // "user" kann ein reserviertes Schlüsselwort sein – daher besser "users"
public class User implements Serializable {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artikel> artikelListe = new ArrayList<>();

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    // JPA erfordert einen Standard-Konstruktor
    public User() {
    }

    // Konstruktor zur Initialisierung
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter & Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public List<Artikel> getArtikelListe() {
        return artikelListe;
    }

    public void setArtikelListe(List<Artikel> artikelListe) {
        this.artikelListe = artikelListe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public static void main(String[] args) {
        // DAOs erstellen
        UserDAO userDAO = new UserDAO();
        ArtikelDAO artikelDAO = new ArtikelDAO();
        artikelDAO.init();

        // Benutzer anlegen
        User admin = new User("admin", "admin123", "admin");
        User scientist1 = new User("science1", "pass123", "scientist");
        User scientist2 = new User("science2", "pass456", "scientist");
        
        userDAO.saveUser(admin);
        userDAO.saveUser(scientist1);
        userDAO.saveUser(scientist2);

        // Artikel anlegen
        Artikel artikel1 = new Artikel();
        artikel1.setLand("Japan");
        artikel1.setJahr(2023);
        artikel1.setCo2Ausstoss(8.5);
        artikel1.setEinheit("t");
        artikel1.setBeschreibung("CO₂-Ausstoß pro Kopf");
        artikel1.setStatus("approved");

        Artikel artikel2 = new Artikel();
        artikel2.setLand("Brasilien");
        artikel2.setJahr(2023); 
        artikel2.setCo2Ausstoss(5.4);
        artikel2.setEinheit("t");
        artikel2.setBeschreibung("CO₂-Ausstoß pro Kopf");
        artikel2.setStatus("approved");

        artikel1.setUser(admin);
        artikel2.setUser(scientist1);

        // Bewertungen hinzufügen
        Bewertung bewertung1 = new Bewertung("Sehr informativ", 4.5);
        bewertung1.setArtikel(artikel1);
        artikel1.addBewertung(bewertung1);

        Bewertung bewertung2 = new Bewertung("Könnte aktueller sein", 3.0);
        bewertung2.setArtikel(artikel2);
        artikel2.addBewertung(bewertung2);

        // Artikel mit Bewertungen speichern
        artikelDAO.saveArtikel(artikel1);
        artikelDAO.saveArtikel(artikel2);

        System.out.println("Benutzer, Artikel und Bewertungen erfolgreich gespeichert.");
    }

}
