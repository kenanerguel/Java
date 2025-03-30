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
            new Artikel("Germany", "Aktueller CO₂-Ausstoß: 8,5 t pro Kopf (Stand 2023).", "germany.png", (new GregorianCalendar(2023, 0, 1).getTime())),
            new Artikel("USA", "Aktueller CO₂-Ausstoß: 15,2 t pro Kopf (Stand 2023).", "usa.png", (new GregorianCalendar(2023, 0, 1).getTime())),
            new Artikel("France", "Aktueller CO₂-Ausstoß: 5,4 t pro Kopf (Stand 2023).", "france.png", (new GregorianCalendar(2023, 0, 1).getTime()))
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

    public List<Number> handleLatestValues(String selectedCountry) {
        List<Number> values = new LinkedList<>();
        String response = artikelDAO.findLatestBeschreibungByCountry(selectedCountry);
        values.add(extractCO2FromBeschreibung(response));
        values.add(extractYearFromBeschreibung(response));
        return values;
    }

    private double extractCO2FromBeschreibung(String beschreibung) {
        try {
            Pattern pattern = Pattern.compile("([\\d,.]+)\\s*t.*?\\(Stand:?\\s*(\\d{4})\\)");
            Matcher matcher = pattern.matcher(beschreibung);
            if (matcher.find()) {
                String co2String = matcher.group(1).replace(",", "."); // "15.2"
                double co2Double = Double.parseDouble(co2String);
                return co2Double; // z.B. 15 (gerundet)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Fehlerindikator
    }

    private int extractYearFromBeschreibung(String beschreibung) {
        try {
            Pattern pattern = Pattern.compile("\\(Stand:?\\s*(\\d{4})\\)");
            Matcher matcher = pattern.matcher(beschreibung);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Fehlerindikator
    }
}
