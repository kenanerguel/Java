package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.model.Bewertung;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mein.projekt.dao.ArtikelDAO;
import com.mein.projekt.model.Artikel;

@Named
@SessionScoped
public class Shop implements Serializable {

    private EntityManagerProvider provider = new EntityManagerProvider();
    private ArtikelDAO artikelDAO = new ArtikelDAO(provider);

    public List<Artikel> getSortiment() {
        return baseSortiment;
    }

    public static final List<Artikel> baseSortiment = Arrays.asList(new Artikel[] {
            new Artikel("Deutschland", 2023, 8.5, "t", "Aktueller CO₂-Ausstoß pro Kopf"),
            new Artikel("USA", 2023, 15.2, "t", "Aktueller CO₂-Ausstoß pro Kopf"),
            new Artikel("Frankreich", 2023, 5.4, "t", "Aktueller CO₂-Ausstoß pro Kopf")
    });

    public Shop() {
    }

    public List<String> getCountries() {
        return artikelDAO.getAllCountries();
    }

    public void handleArtikel(Artikel artikel, CurrentUser user) {
        artikel.setUser(user.getUser());
        Bewertung bewertung = new Bewertung("Sehr informativ", 4.5);

        bewertung.setArtikel(artikel);
        artikel.addBewertung(bewertung);

        artikelDAO.saveArtikel(artikel);
    }

    public List<Number> handleLatestValues(String country) {
        return artikelDAO.getLatestValues(country);
    }
}
