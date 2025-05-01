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
import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.auth.Shop;
import com.mein.projekt.model.User;
import jakarta.persistence.NoResultException;

@Named
@ApplicationScoped
public class ArtikelDAO {

    private static final Logger LOGGER = Logger.getLogger(ArtikelDAO.class.getName());
    
    @Inject
    private EntityManagerProvider entityManagerProvider;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public ArtikelDAO() {
        // Default constructor for CDI
    }

    @PostConstruct
    public void init() {
        try {
            this.entityManager = entityManagerProvider.getEntityManager();
            this.criteriaBuilder = entityManager.getCriteriaBuilder();

            // Initialisierung der Daten, falls noch keine Datensätze vorhanden sind
            long count = getArtikelCount();
            LOGGER.info("Aktuell gibt es " + count + " CO₂-Datensätze.");

            if (count == 0) {
                LOGGER.info("Initialisierung der CO₂-Datensätze.");
                EntityTransaction t = getAndBeginTransaction();
                try {
                    // Hier werden die Basis-Datensätze aus Shop.getBaseSortiment() eingefügt
                    for (Artikel art : Shop.getBaseSortiment()) {
                        persist(art);
                    }
                    t.commit();
                } catch (Exception e) {
                    t.rollback();
                    throw e;
                }
            }

        } catch (Exception e) {
            LOGGER.severe("Fehler bei der Initialisierung des ArtikelDAO: " + e.getMessage());
            throw new RuntimeException("Failed to initialize ArtikelDAO", e);
        }
    }

    @Transactional
    public List<String> getAllCountries() {
        try {
            TypedQuery<String> query = entityManager.createQuery(
                    "SELECT DISTINCT a.land FROM Artikel a ORDER BY a.land", String.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Abrufen der Länder: " + e.getMessage());
            return Collections.emptyList();
        }
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
        try {
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<Artikel> root = countQuery.from(Artikel.class);
            countQuery.select(criteriaBuilder.count(root));
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Zählen der Artikel: " + e.getMessage());
            return 0;
        }
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

    private EntityTransaction getAndBeginTransaction() {
        EntityTransaction t = entityManager.getTransaction();
        if (!t.isActive()) {
            t.begin();
        }
        return t;
    }

    @Transactional
    public void persist(Artikel artikel) {
        try {
            entityManager.persist(artikel);
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Speichern des Artikels: " + e.getMessage());
            throw new RuntimeException("Failed to persist Artikel", e);
        }
    }

    @Transactional
    public void merge(Artikel artikel) {
        try {
            entityManager.merge(artikel);
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Aktualisieren des Artikels: " + e.getMessage());
            throw new RuntimeException("Failed to merge Artikel", e);
        }
    }

    @Transactional
    public void remove(Artikel artikel) {
        try {
            entityManager.remove(entityManager.contains(artikel) ? artikel : entityManager.merge(artikel));
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Löschen des Artikels: " + e.getMessage());
            throw new RuntimeException("Failed to remove Artikel", e);
        }
    }

    public List<Artikel> getAllArtikel() {
        try {
            CriteriaQuery<Artikel> query = criteriaBuilder.createQuery(Artikel.class);
            Root<Artikel> root = query.from(Artikel.class);
            query.select(root);
            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Abrufen aller Artikel: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Artikel findById(Long id) {
        try {
            return entityManager.find(Artikel.class, id);
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Suchen des Artikels mit ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    public List<Artikel> findByLand(String land) {
        try {
            CriteriaQuery<Artikel> query = criteriaBuilder.createQuery(Artikel.class);
            Root<Artikel> root = query.from(Artikel.class);
            query.select(root).where(criteriaBuilder.equal(root.get("land"), land));
            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Suchen der Artikel für Land " + land + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public void saveArtikel(Artikel artikel) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            if (!transaction.isActive()) {
                transaction.begin();
            }
            entityManager.persist(artikel);
            transaction.commit();
            LOGGER.info("Artikel erfolgreich gespeichert: " + artikel.getId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("Fehler beim Speichern des Artikels: " + e.getMessage());
            throw new RuntimeException("Failed to save Artikel", e);
        }
    }

    @Transactional
    public void updateArtikel(Artikel artikel) {
        try {
            entityManager.merge(artikel);
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Aktualisieren des Artikels: " + e.getMessage());
            throw new RuntimeException("Failed to update Artikel", e);
        }
    }

    public List<Artikel> getPendingArtikel() {
        try {
            return entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.status = 'pending' ORDER BY a.erstelltAm DESC", 
                Artikel.class).getResultList();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Laden der ausstehenden Artikel: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Artikel> getArtikelByUser(User user) {
        try {
            return entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.user = :user ORDER BY a.erstelltAm DESC", 
                Artikel.class)
                .setParameter("user", user)
                .getResultList();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Laden der Artikel für Benutzer " + user.getUsername() + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Number> getLatestValues(String country) {
        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                    "SELECT a FROM Artikel a WHERE a.land = :country ORDER BY a.jahr DESC", Artikel.class);
            query.setParameter("country", country);
            query.setMaxResults(1);
            
            List<Artikel> results = query.getResultList();
            LOGGER.info("Gefundene Ergebnisse für " + country + ": " + results.size());
            
            if (!results.isEmpty()) {
                Artikel latest = results.get(0);
                LOGGER.info("Gefundene Daten: CO2=" + latest.getCo2Ausstoss() + 
                           ", Jahr=" + latest.getJahr());
                return List.of(latest.getCo2Ausstoss(), latest.getJahr());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Abrufen der neuesten Werte für " + country + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Artikel getAktuellerArtikelByLand(String land) {
        try {
            TypedQuery<Artikel> query = entityManager.createQuery(
                "SELECT a FROM Artikel a WHERE a.land = :land AND a.status = 'approved' ORDER BY a.jahr DESC",
                Artikel.class);
            query.setParameter("land", land);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Kein aktueller Artikel gefunden für Land: " + land);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Abrufen des aktuellen Artikels für " + land + ": " + e.getMessage());
            return null;
        }
    }
}
