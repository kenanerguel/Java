package com.mein.projekt.dao;

import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.auth.Shop;
import com.mein.projekt.model.User;

@Named
@ApplicationScoped
public class ArtikelDAO {

    private static final Logger LOGGER = Logger.getLogger(ArtikelDAO.class.getName());

    @Inject
    private EntityManagerProvider entityManagerProvider;
    
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public ArtikelDAO() {
        // Standard-Konstruktor für CDI
    }

    public void setEntityManagerProvider(EntityManagerProvider provider) {
        this.entityManagerProvider = provider;
    }

    @PostConstruct
    public void init() {
        this.entityManager = entityManagerProvider.getEntityManager();
        try {
            criteriaBuilder = entityManager.getCriteriaBuilder();

            // Initialisierung der Daten, falls noch keine Datensätze vorhanden sind
            long count = getArtikelCount();
            System.err.println("Aktuell gibt es " + count + " CO₂-Datensätze.");

            if (count == 0) {
                System.err.println("Initialisierung der CO₂-Datensätze.");
                EntityTransaction t = getAndBeginTransaction();
                // Hier werden die Basis-Datensätze aus Shop.baseSortiment eingefügt
                for (Artikel art : Shop.baseSortiment) {
                    persist(art);
                }
                t.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        LOGGER.info("ArtikelDAO wurde initialisiert");
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
            e.printStackTrace();
            return null;
        }
    }


    public long getArtikelCount() {
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        cq.select(criteriaBuilder.count(cq.from(Artikel.class)));
        return entityManager.createQuery(cq).getSingleResult();
    }

    public Artikel getArtikelAtIndex(int pos) {
        CriteriaQuery<Artikel> cq = criteriaBuilder.createQuery(Artikel.class);
        Root<Artikel> root = cq.from(Artikel.class);
        return entityManager.createQuery(cq)
                .setMaxResults(1)
                .setFirstResult(pos)
                .getSingleResult();
    }

    public List<String> getAlleBilder() {
        return entityManager.createQuery("SELECT a.bild FROM Artikel a GROUP BY a.bild", String.class)
                .getResultList();
    }

    public EntityTransaction getAndBeginTransaction() {
        EntityTransaction tran = entityManager.getTransaction();
        tran.begin();
        return tran;
    }

    public void merge(Artikel art) {
        entityManager.merge(art);
    }

    public void persist(Artikel art) {
        entityManager.persist(art);
    }

    public void removeArtikel(Artikel art) {
        // TODO: Implementiere die Löschlogik (z. B. mit CriteriaDelete)
    }

    public static void main(String[] args) {
        EntityManagerProvider provider = new EntityManagerProvider();
        ArtikelDAO dao = new ArtikelDAO();
        dao.entityManagerProvider = provider;
        dao.init();
        List<Artikel> artikelListe = dao.entityManager.createQuery("SELECT a FROM Artikel a", Artikel.class).getResultList();
        for (Artikel art : artikelListe) {
            System.out.println(art.getBeschreibung());
        }
        System.err.println("Es gibt " + dao.getArtikelCount() + " CO₂-Datensätze.");
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
        System.out.println("Suche nach Daten für Land: " + country);
        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                    "SELECT a FROM Artikel a WHERE a.land = :country ORDER BY a.jahr DESC", Artikel.class);
            query.setParameter("country", country);
            query.setMaxResults(1);
            
            List<Artikel> results = query.getResultList();
            System.out.println("Gefundene Ergebnisse: " + results.size());
            
            if (!results.isEmpty()) {
                Artikel latest = results.get(0);
                System.out.println("Gefundene Daten: CO2=" + latest.getCo2Ausstoss() + 
                                 ", Jahr=" + latest.getJahr() + 
                                 ", Status=" + latest.getStatus());
                return List.of(latest.getCo2Ausstoss(), latest.getJahr());
            }
            System.out.println("Keine Daten gefunden für: " + country);
            return List.of(-1.0, -1);
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Daten: " + e.getMessage());
            e.printStackTrace();
            return List.of(-1.0, -1);
        }
    }

    public Artikel getAktuellerArtikelByLand(String land) {
        System.out.println("Suche nach aktuellen Daten für Land: " + land);
        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.land = :land AND a.status = 'approved' " +
                "ORDER BY a.jahr DESC, a.erstelltAm DESC", 
                Artikel.class);
            query.setParameter("land", land);
            query.setMaxResults(1);
            
            List<Artikel> results = query.getResultList();
            System.out.println("Anzahl gefundener Ergebnisse: " + results.size());
            
            if (!results.isEmpty()) {
                Artikel artikel = results.get(0);
                System.out.println("Gefundene Daten: Land=" + artikel.getLand() + 
                                 ", Jahr=" + artikel.getJahr() + 
                                 ", CO2=" + artikel.getCo2Ausstoss() + 
                                 ", Status=" + artikel.getStatus() +
                                 ", Erstellt am=" + artikel.getErstelltAm());
                return artikel;
            }
            System.out.println("Keine Daten gefunden für Land: " + land);
            return null;
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Daten: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
