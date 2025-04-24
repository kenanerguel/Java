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
