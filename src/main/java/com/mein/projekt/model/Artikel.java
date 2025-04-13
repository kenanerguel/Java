package com.mein.projekt.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Artikel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int nr;

    private String land;
    private int jahr;
    private double co2Ausstoss;
    private String einheit;
    private String beschreibung;
    private String status = "pending"; // pending, approved, rejected

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.DATE)
    private Date erstelltAm;

    @OneToMany(mappedBy = "artikel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Bewertung> bewertungen = new ArrayList<>();

    public Artikel() {}

    public Artikel(String land, int jahr, double co2Ausstoss, String einheit, String beschreibung) {
        this.land = land;
        this.jahr = jahr;
        this.co2Ausstoss = co2Ausstoss;
        this.einheit = einheit;
        this.beschreibung = beschreibung;
        this.erstelltAm = new Date();
    }

    // Getter & Setter
    public int getNr() { return nr; }
    public void setNr(int nr) { this.nr = nr; }
    
    public String getLand() { return land; }
    public void setLand(String land) { this.land = land; }
    
    public int getJahr() { return jahr; }
    public void setJahr(int jahr) { this.jahr = jahr; }
    
    public double getCo2Ausstoss() { return co2Ausstoss; }
    public void setCo2Ausstoss(double co2Ausstoss) { this.co2Ausstoss = co2Ausstoss; }
    
    public String getEinheit() { return einheit; }
    public void setEinheit(String einheit) { this.einheit = einheit; }
    
    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(Date erstelltAm) { this.erstelltAm = erstelltAm; }
    
    public List<Bewertung> getBewertungen() { return bewertungen; }
    public void addBewertung(Bewertung bewertung) { bewertungen.add(bewertung); }
    
    public void setUser(User user) { this.user = user; }
    public User getUser() { return this.user; }
}
