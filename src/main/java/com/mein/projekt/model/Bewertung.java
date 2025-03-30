package com.mein.projekt.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Bewertung implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private double wert;
    private String text;

    @ManyToOne
    @JoinColumn(name = "artikel_id")
    private Artikel artikel;


    // Default-Konstruktor (muss vorhanden sein)
    public Bewertung() {
    }

    // Optionaler Konstruktor
    public Bewertung(String text, double wert) {
        this.text = text;
        this.wert = wert;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getWert() {
        return wert;
    }

    public void setWert(double wert) {
        this.wert = wert;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setArtikel(Artikel artikel) { this.artikel = artikel; }
}
