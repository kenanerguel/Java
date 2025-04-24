package com.mein.projekt.auth;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.stream.Collectors;

@Named
@ApplicationScoped
public class Shop {

    private static final Logger LOGGER = Logger.getLogger(Shop.class.getName());

    @Inject
    private EntityManager entityManager;

    public static final List<Artikel> baseSortiment = new ArrayList<>();

    static {
        // Beispiel-Artikel für die Initialisierung
        Artikel artikel1 = new Artikel();
        artikel1.setLand("Deutschland");
        artikel1.setJahr(2023);
        artikel1.setCo2Ausstoss(7.7);
        artikel1.setEinheit("t");
        artikel1.setBeschreibung("CO₂-Ausstoß pro Kopf");
        artikel1.setStatus("approved");
        baseSortiment.add(artikel1);

        Artikel artikel2 = new Artikel();
        artikel2.setLand("Frankreich");
        artikel2.setJahr(2023);
        artikel2.setCo2Ausstoss(4.5);
        artikel2.setEinheit("t");
        artikel2.setBeschreibung("CO₂-Ausstoß pro Kopf");
        artikel2.setStatus("approved");
        baseSortiment.add(artikel2);
    }

    public List<Artikel> getSortiment() {
        return baseSortiment;
    }

    public List<String> getCountries() {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - kann keine Länder abrufen");
            return new ArrayList<>();
        }

        try {
            TypedQuery<String> query = entityManager.createQuery(
                "SELECT DISTINCT a.land FROM Artikel a ORDER BY a.land",
                String.class
            );
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Länder", e);
            return new ArrayList<>();
        }
    }

    public void handleArtikel(Artikel artikel, User user) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - kann Artikel nicht verarbeiten");
            return;
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            artikel.setUser(user);
            artikel.setStatus(user.isAdmin() ? "approved" : "pending");
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
            LOGGER.severe("EntityManager ist null - kann Artikel nicht aktualisieren");
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

    public List<Number> handleLatestValues(String country) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - kann keine Werte abrufen");
            return List.of(-1.0, -1);
        }

        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.land = :land AND a.status = 'approved' " +
                "ORDER BY a.jahr DESC",
                Artikel.class
            )
            .setParameter("land", country)
            .setMaxResults(1);

            List<Artikel> results = query.getResultList();
            if (!results.isEmpty()) {
                Artikel latest = results.get(0);
                return List.of(latest.getCo2Ausstoss(), latest.getJahr());
            }
            return List.of(-1.0, -1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Werte für " + country, e);
            return List.of(-1.0, -1);
        }
    }

    public List<Artikel> getPendingArtikel() {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - kann keine ausstehenden Artikel abrufen");
            return new ArrayList<>();
        }

        try {
            return entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.status = 'pending'", 
                Artikel.class
            ).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der ausstehenden Artikel", e);
            return new ArrayList<>();
        }
    }

    public List<Artikel> getArtikelByUser(User user) {
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - kann keine Artikel für Benutzer abrufen");
            return new ArrayList<>();
        }

        try {
            return entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.user = :user", 
                Artikel.class
            )
            .setParameter("user", user)
            .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Artikel für Benutzer: " + user.getUsername(), e);
            return new ArrayList<>();
        }
    }

    public void init() {
        LOGGER.info("Initialisiere Shop");
        if (entityManager == null) {
            LOGGER.severe("EntityManager ist null - konnte nicht initialisiert werden");
            return;
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (Artikel artikel : baseSortiment) {
                entityManager.persist(artikel);
            }
            transaction.commit();
            LOGGER.info("Basis-Sortiment erfolgreich initialisiert");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Fehler beim Initialisieren des Shops", e);
        }
    }
}
