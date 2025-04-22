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
import jakarta.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("shop")
@SessionScoped
public class Shop implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Shop.class.getName());
    
    @Inject
    private EntityManagerProvider entityManagerProvider;
    
    private ArtikelDAO artikelDAO;
    
    private List<String> countries;
    private String selectedCountry;
    private Artikel currentArtikel;

    public List<Artikel> getSortiment() {
        return baseSortiment;
    }

    public static final List<Artikel> baseSortiment = Arrays.asList(new Artikel[] {
            new Artikel("Deutschland", 2023, 8.5, "t", "Aktueller CO₂-Ausstoß pro Kopf"),
            new Artikel("USA", 2023, 15.2, "t", "Aktueller CO₂-Ausstoß pro Kopf"),
            new Artikel("Frankreich", 2023, 5.4, "t", "Aktueller CO₂-Ausstoß pro Kopf")
    });

    public Shop() {
        // Leerer Konstruktor für CDI
    }
    
    @Inject
    public void setArtikelDAO(EntityManagerProvider entityManagerProvider) {
        try {
            this.artikelDAO = new ArtikelDAO(entityManagerProvider);
            LOGGER.info("ArtikelDAO erfolgreich initialisiert");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Initialisieren des ArtikelDAO", e);
        }
    }

    public List<String> getCountries() {
        if (countries == null) {
            initCountries();
        }
        return countries;
    }
    
    private void initCountries() {
        try {
            countries = artikelDAO.getAllCountries();
            LOGGER.info("Länder erfolgreich geladen: " + countries.size());
        } catch (Exception e) {
            countries = new ArrayList<>();
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Länder", e);
        }
    }

    public void onCountryChange() {
        if (selectedCountry != null && !selectedCountry.isEmpty()) {
            try {
                LOGGER.info("Lade Artikel für Land: " + selectedCountry);
                currentArtikel = artikelDAO.getAktuellerArtikelByLand(selectedCountry);
                if (currentArtikel != null) {
                    LOGGER.info("Artikel gefunden: " + currentArtikel.getCo2Ausstoss() + " für " + selectedCountry);
                } else {
                    LOGGER.warning("Kein Artikel für Land gefunden: " + selectedCountry);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Fehler beim Laden des Artikels für Land: " + selectedCountry, e);
                currentArtikel = null;
            }
        } else {
            currentArtikel = null;
        }
    }
    
    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public Artikel getCurrentArtikel() {
        return currentArtikel;
    }

    /**
     * Handhabt das Speichern eines neuen Artikels von einem Benutzer.
     */
    public void handleArtikel(Artikel artikel, User user) {
        try {
            LOGGER.info("Speichere Artikel für " + artikel.getLand() + " mit CO2: " + artikel.getCo2Ausstoss());
            artikelDAO.persistArtikel(artikel, user);
            LOGGER.info("Artikel erfolgreich gespeichert");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Artikels", e);
        }
    }

    public List<Number> handleLatestValues(String country) {
        return artikelDAO.getLatestValues(country);
    }
}
