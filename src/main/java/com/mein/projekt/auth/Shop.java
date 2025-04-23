package com.mein.projekt.auth;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import com.mein.projekt.dao.ArtikelDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static final List<Artikel> baseSortiment = new ArrayList<>(Arrays.asList(
            createArtikel("Deutschland", 8.5, 2023),
            createArtikel("USA", 15.2, 2023),
            createArtikel("Frankreich", 5.4, 2023)
    ));
    
    private static Artikel createArtikel(String land, double co2, int jahr) {
        Artikel a = new Artikel();
        a.setLand(land);
        a.setCo2Ausstoss(co2);
        a.setJahr(jahr);
        a.setBeschreibung("Wissenschaftler");
        a.setStatus("approved");
        return a;
    }

    public Shop() {
        // Leerer Konstruktor für CDI
    }
    
    @Inject
    public void setArtikelDAO(EntityManagerProvider entityManagerProvider) {
        try {
            this.artikelDAO = new ArtikelDAO();
            this.artikelDAO.setEntityManagerProvider(entityManagerProvider);
            this.artikelDAO.init();
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
            artikel.setUser(user);
            artikelDAO.saveArtikel(artikel);
            LOGGER.info("Artikel erfolgreich gespeichert");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Artikels", e);
        }
    }

    /**
     * Aktualisiert einen bestehenden Artikel.
     */
    public void updateArtikel(Artikel artikel) {
        try {
            LOGGER.info("Aktualisiere Artikel für " + artikel.getLand() + " mit Status: " + artikel.getStatus());
            artikelDAO.updateArtikel(artikel);
            LOGGER.info("Artikel erfolgreich aktualisiert");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Artikels", e);
        }
    }

    /**
     * Holt alle ausstehenden Artikel.
     */
    public List<Artikel> getPendingArtikel() {
        try {
            LOGGER.info("Hole ausstehende Artikel");
            return artikelDAO.getPendingArtikel();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der ausstehenden Artikel", e);
            return new ArrayList<>();
        }
    }

    /**
     * Holt alle Artikel eines bestimmten Benutzers.
     */
    public List<Artikel> getArtikelByUser(User user) {
        try {
            LOGGER.info("Hole Artikel für Benutzer: " + user.getUsername());
            return artikelDAO.getArtikelByUser(user);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Artikel für Benutzer: " + user.getUsername(), e);
            return new ArrayList<>();
        }
    }

    public List<Number> handleLatestValues(String country) {
        return artikelDAO.getLatestValues(country);
    }
}
