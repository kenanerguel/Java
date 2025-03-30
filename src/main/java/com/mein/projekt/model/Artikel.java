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

    private String name;
    private String beschreibung;
    private String bild;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Ein Artikel gehört einem User


    @Temporal(TemporalType.DATE)
    private Date verfuegbarAb;

    @OneToMany(mappedBy = "artikel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Bewertung> bewertungen = new ArrayList<>();

    public Artikel() {}

    public Artikel(String name, String beschreibung, String bild) {
        this(name, beschreibung, bild, new Date());
    }

    public Artikel(String name, String beschreibung, String bild, Date verfuegbarAb) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.bild = bild;
        this.verfuegbarAb = verfuegbarAb;
    }

    // Getter & Setter
    public int getNr() { return nr; }
    public String getBeschreibung() { return beschreibung; }
    public void setNr(int nr) { this.nr = nr; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getText() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public String getBild() { return bild; }
    public void setBild(String bild) { this.bild = bild; }
    public Date getVerfuegbarAb() { return verfuegbarAb; }
    public void setVerfuegbarAb(Date verfuegbarAb) { this.verfuegbarAb = verfuegbarAb; }
    public List<Bewertung> getBewertungen() { return bewertungen; }
    public void addBewertung(Bewertung bewertung) { bewertungen.add(bewertung); }
    public void setUser(User user) { this.user = user; }
    public User getUser() { return this.user; }
}
