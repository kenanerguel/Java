package com.mein.projekt.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import com.mein.projekt.model.Bewertung;

@Entity
public class Artikel implements Serializable {

    @Id
    @GeneratedValue
    private int nr; // Primärschlüssel, eindeutige ID des Datensatzes

    // Im Kontext von "Like Hero To Zero" repräsentiert 'name' den Namen des Landes,
    // für das der aktuelle CO₂-Ausstoß angezeigt wird.
    private String name;

    // 'text' enthält die Beschreibung oder weitere Informationen zum CO₂-Ausstoß,
    // z. B. Hinweise zu Trends oder Besonderheiten.
    private String text;

    // 'bild' speichert den Pfad zu einem Bild oder Diagramm, das den Zustand oder die Daten visualisiert.
    private String bild;

    // 'verfuegbarAb' gibt an, ab wann die angezeigten Daten gültig bzw. erfasst wurden.
    private Date verfuegbarAb;

    // Optional: Bewertungen – in diesem Projekt werden diese vermutlich nicht genutzt,
    // können aber beispielsweise für Feedback in einer erweiterten Version verwendet werden.
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "artikel_id")  // Fügt in der Bewertung-Tabelle eine Spalte 'artikel_id' als Fremdschlüssel hinzu
    private List<Bewertung> bewertungen = new ArrayList<>();

    public Artikel() {
    }

    public Artikel(String name, String text, String bild) {
        this(name, text, bild, new Date(0));
    }

    public Artikel(String name, String text, String bild, Date verfuegbarAb) {
        this.name = name;
        this.text = text;
        this.bild = bild;
        this.verfuegbarAb = verfuegbarAb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBild() {
        return bild;
    }

    public void setBild(String bild) {
        this.bild = bild;
    }

    public Date getVerfuegbarAb() {
        return verfuegbarAb;
    }

    public void setVerfuegbarAb(Date verfuegbarAb) {
        this.verfuegbarAb = verfuegbarAb;
    }

    public List <Bewertung> getBewertungen() {
        return bewertungen;
    }

    public void addBewertung(Bewertung bewertung) {
        bewertungen.add(bewertung);
    }
}

