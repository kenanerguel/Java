package com.mein.projekt.dao;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class ArtikelDAO {

    private static final Logger LOGGER = Logger.getLogger(ArtikelDAO.class.getName());

    @Inject
    private EntityManager entityManager;

    private CriteriaBuilder criteriaBuilder;

    public ArtikelDAO() {
        LOGGER.info("ArtikelDAO wurde mit Standard-Konstruktor erstellt");
    }

    public void init() {
        try {
            criteriaBuilder = entityManager.getCriteriaBuilder();
            // Initialisierung der Daten, falls noch keine Datensätze vorhanden sind
            long count = getArtikelCount();
            LOGGER.info("Aktuell gibt es " + count + " CO₂-Datensätze.");

            if (count == 0) {
                LOGGER.info("Initialisierung der CO₂-Datensätze.");
                EntityTransaction t = entityManager.getTransaction();
                t.begin();
                // Hier können Sie Ihre Basis-Datensätze einfügen
                t.commit();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler bei der Initialisierung", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllCountries() {
        TypedQuery<String> query = entityManager.createQuery(
                "SELECT DISTINCT a.land FROM Artikel a ORDER BY a.land", String.class);
        return query.getResultList();
    }

    public String findLatestBeschreibungByCountry(String selectedCountry) {
        try {
            return entityManager.createQuery(
                    "SELECT a.beschreibung FROM Artikel a WHERE a.land = :name ORDER BY a.jahr DESC", String.class)
                    .setParameter("name", selectedCountry)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Beschreibung für " + selectedCountry, e);
            return null;
        }
    }

    public long getArtikelCount() {
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        cq.select(criteriaBuilder.count(cq.from(Artikel.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }

    public void saveArtikel(Artikel artikel) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return;
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(artikel);
            transaction.commit();
            LOGGER.info("Artikel für " + artikel.getLand() + " erfolgreich gespeichert");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Artikels", e);
        }
    }

    public void updateArtikel(Artikel artikel) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return;
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(artikel);
            transaction.commit();
            LOGGER.info("Artikel für " + artikel.getLand() + " erfolgreich aktualisiert");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Artikels", e);
        }
    }

    public List<Artikel> getPendingArtikel() {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return new ArrayList<>();
        }

        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.status = 'pending' ORDER BY a.erstellungsdatum DESC",
                Artikel.class
            );
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der ausstehenden Artikel", e);
            return new ArrayList<>();
        }
    }

    public List<Artikel> getArtikelByUser(User user) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return new ArrayList<>();
        }

        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.user = :user ORDER BY a.erstellungsdatum DESC",
                Artikel.class
            );
            query.setParameter("user", user);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Artikel für Benutzer: " + user.getUsername(), e);
            return new ArrayList<>();
        }
    }

    public List<Number> getLatestValues(String country) {
        LOGGER.info("Suche nach Daten für Land: " + country);
        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                    "SELECT a FROM Artikel a WHERE a.land = :country ORDER BY a.jahr DESC", Artikel.class);
            query.setParameter("country", country);
            query.setMaxResults(1);
            
            List<Artikel> results = query.getResultList();
            LOGGER.info("Gefundene Ergebnisse: " + results.size());
            
            if (!results.isEmpty()) {
                Artikel latest = results.get(0);
                LOGGER.info("Gefundene Daten: CO2=" + latest.getCo2Ausstoss() + 
                           ", Jahr=" + latest.getJahr() + 
                           ", Status=" + latest.getStatus());
                return List.of(latest.getCo2Ausstoss(), latest.getJahr());
            }
            LOGGER.info("Keine Daten gefunden für: " + country);
            return List.of(-1.0, -1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Daten für " + country, e);
            return List.of(-1.0, -1);
        }
    }

    public Artikel getAktuellerArtikelByLand(String land) {
        LOGGER.info("Suche nach aktuellen Daten für Land: " + land);
        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.land = :land AND a.status = 'approved' " +
                "ORDER BY a.jahr DESC, a.erstelltAm DESC", 
                Artikel.class);
            query.setParameter("land", land);
            query.setMaxResults(1);
            
            List<Artikel> results = query.getResultList();
            LOGGER.info("Anzahl gefundener Ergebnisse: " + results.size());
            
            if (!results.isEmpty()) {
                Artikel artikel = results.get(0);
                LOGGER.info("Gefundene Daten: Land=" + artikel.getLand() + 
                           ", Jahr=" + artikel.getJahr() + 
                           ", CO2=" + artikel.getCo2Ausstoss() + 
                           ", Status=" + artikel.getStatus() +
                           ", Erstellt am=" + artikel.getErstelltAm());
                return artikel;
            }
            LOGGER.info("Keine Daten gefunden für Land: " + land);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Daten für " + land, e);
            return null;
        }
    }
}
