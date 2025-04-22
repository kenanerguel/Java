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

@Named("shop")
@SessionScoped
public class Shop implements Serializable {

    private EntityManagerProvider provider = new EntityManagerProvider();
    @Inject
    private ArtikelDAO artikelDAO;

    private List<String> countries;
    private String selectedCountry;
    private Artikel aktuellerArtikel;

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

    public void init() {
        countries = artikelDAO.getAllCountries();
        System.out.println("Verfügbare Länder: " + countries);
    }

    public void onCountryChange() {
        System.out.println("Land geändert zu: " + selectedCountry);
        if (selectedCountry != null && !selectedCountry.isEmpty()) {
            aktuellerArtikel = artikelDAO.getAktuellerArtikelByLand(selectedCountry);
            if (aktuellerArtikel != null) {
                System.out.println("Gefundene Daten: CO2=" + aktuellerArtikel.getCo2Ausstoss() + 
                                 ", Jahr=" + aktuellerArtikel.getJahr());
            } else {
                System.out.println("Keine Daten gefunden für: " + selectedCountry);
            }
        } else {
            aktuellerArtikel = null;
        }
    }

    public void handleArtikel(Artikel artikel, CurrentUser user) {
        System.out.println("Speichere neuen Artikel: Land=" + artikel.getLand() + 
                         ", Jahr=" + artikel.getJahr() + 
                         ", CO2=" + artikel.getCo2Ausstoss());
        
        try {
            artikel.setUser(user.getUser());
            artikel.setStatus("approved"); // Stelle sicher, dass der Status gesetzt ist
            
            // Starte eine neue Transaktion
            var transaction = provider.getEntityManager().getTransaction();
            transaction.begin();
            
            try {
                artikelDAO.persist(artikel);
                transaction.commit();
                System.out.println("Artikel erfolgreich gespeichert");
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println("Fehler beim Speichern des Artikels: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Allgemeiner Fehler in handleArtikel: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<Number> handleLatestValues(String country) {
        return artikelDAO.getLatestValues(country);
    }

    // Getter & Setter
    public List<String> getCountries() { return countries; }
    public void setCountries(List<String> countries) { this.countries = countries; }
    
    public String getSelectedCountry() { return selectedCountry; }
    public void setSelectedCountry(String selectedCountry) { this.selectedCountry = selectedCountry; }
    
    public Artikel getAktuellerArtikel() { return aktuellerArtikel; }
    public void setAktuellerArtikel(Artikel aktuellerArtikel) { this.aktuellerArtikel = aktuellerArtikel; }
}
