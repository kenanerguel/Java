package com.mein.projekt.model;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.inject.Inject;
import com.mein.projekt.util.EntityManagerProvider;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@Stateless
public class Shop {
    private static final Logger LOGGER = Logger.getLogger(Shop.class.getName());

    @Inject
    private EntityManagerProvider entityManagerProvider;

    private EntityManager getEntityManager() {
        return entityManagerProvider.getEntityManager();
    }

    public List<Artikel> getPendingArtikel() {
        EntityManager em = getEntityManager();
        TypedQuery<Artikel> query = em.createQuery(
            "SELECT a FROM Artikel a WHERE a.status = 'pending' ORDER BY a.createdAt DESC", 
            Artikel.class
        );
        return query.getResultList();
    }
    
    public List<Artikel> getArtikelByUser(User user) {
        EntityManager em = getEntityManager();
        TypedQuery<Artikel> query = em.createQuery(
            "SELECT a FROM Artikel a WHERE a.user = :user ORDER BY a.createdAt DESC",
            Artikel.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    public void handleArtikel(Artikel artikel, User user) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Date now = new Date();
            LOGGER.info("Handling Artikel with user: " + user.getUsername());
            
            if (artikel.getId() == null) {
                // Neuer Artikel
                LOGGER.info("Creating new Artikel");
                artikel.setCreatedAt(now);
                artikel.setUpdatedAt(now);
                artikel.setCreatedBy(user);
                artikel.setUpdatedBy(user);
                artikel.setStatus("pending");
                artikel.setErstelltAm(now);
                artikel.setUser(user);
                em.persist(artikel);
                em.flush();
                LOGGER.info("Artikel persisted");
            } else {
                // Existierender Artikel
                LOGGER.info("Updating existing Artikel with ID: " + artikel.getId());
                Artikel managedArtikel = em.find(Artikel.class, artikel.getId());
                if (managedArtikel != null) {
                    managedArtikel.setUpdatedAt(now);
                    managedArtikel.setUpdatedBy(user);
                    managedArtikel.setStatus(artikel.getStatus());
                    managedArtikel.setBeschreibung(artikel.getBeschreibung());
                    em.merge(managedArtikel);
                    em.flush();
                }
            }
            
            tx.commit();
            LOGGER.info("Transaction committed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in handleArtikel", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Fehler beim Speichern des Artikels: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                entityManagerProvider.closeEntityManager();
            }
        }
    }

    public void handleArtikel(Artikel artikel) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Date now = new Date();
            LOGGER.info("Updating Artikel with ID: " + artikel.getId());
            
            Artikel managedArtikel = em.find(Artikel.class, artikel.getId());
            if (managedArtikel != null) {
                managedArtikel.setUpdatedAt(now);
                managedArtikel.setStatus(artikel.getStatus());
                em.merge(managedArtikel);
            }
            
            tx.commit();
            LOGGER.info("Transaction committed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in handleArtikel", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Fehler beim Aktualisieren des Artikels: " + e.getMessage(), e);
        } finally {
            entityManagerProvider.closeEntityManager();
        }
    }
    
    public List<String> getCountries() {
        EntityManager em = getEntityManager();
        TypedQuery<String> query = em.createQuery(
            "SELECT DISTINCT a.land FROM Artikel a WHERE a.status = 'approved' ORDER BY a.land",
            String.class
        );
        return query.getResultList();
    }
} 