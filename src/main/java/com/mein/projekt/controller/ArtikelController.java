package com.mein.projekt.controller;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import jakarta.persistence.EntityTransaction;

import com.mein.projekt.dao.ArtikelDAO;
import com.mein.projekt.model.Artikel;
import com.mein.projekt.auth.CurrentUser;

@Named
@SessionScoped
public class ArtikelController implements Serializable {
    private int index = 0;

    @Inject
    private ArtikelDAO artikelDAO;

    @Inject
    private CurrentUser currentUser;

    // Artikel repräsentiert in diesem Kontext einen CO₂-Datensatz (z.B. eines Landes)
    private Artikel artikel;

    // Gibt den aktuellen Datensatz zurück
    public Artikel getArtikel() {
        artikel = artikelDAO.getArtikelAtIndex(index);
        return artikel;
    }

    // Wechselt zum nächsten Datensatz und speichert den aktuellen
    public void vor() {
        System.err.println("Speichere Datensatz: " + artikel.getName());
        EntityTransaction t = artikelDAO.getAndBeginTransaction();
        artikelDAO.merge(artikel);
        t.commit();
        if (index < artikelDAO.getArtikelCount() - 1) {
            index++;
        }
    }

    // Wechselt zum vorherigen Datensatz und speichert den aktuellen
    public void zurueck() {
        System.err.println("Speichere Datensatz: " + artikel.getName());
        EntityTransaction t = artikelDAO.getAndBeginTransaction();
        artikelDAO.merge(artikel);
        t.commit();
        if (index > 0) {
            index--;
        }
    }

    // Entfernt den aktuellen Datensatz
    public void removeArtikel() {
        if (artikelDAO.getArtikelCount() > 0) {
            artikelDAO.removeArtikel(artikel);
        }
    }

    // Getter für den aktuellen Index
    public int getIndex() {
        return index;
    }

    // Gibt den maximalen Index (basierend auf der Anzahl der Datensätze) zurück
    public int getMaxIndex() {
        return (int) artikelDAO.getArtikelCount() - 1;
    }

    // Liefert eine Liste aller Bildpfade (z.B. für Diagramme oder Karten)
    public List<String> getAlleBilder() {
        return artikelDAO.getAlleBilder();
    }
}
