package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class EntityManagerProvider implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EntityManagerProvider.class.getName());
    private static final String PERSISTENCE_UNIT_NAME = "co2PU";
    
    private EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> entityManager = new ThreadLocal<>();
    
    @PostConstruct
    public void init() {
        try {
            LOGGER.info("Initializing EntityManagerFactory with " + PERSISTENCE_UNIT_NAME);
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            LOGGER.info("EntityManagerFactory successfully initialized");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing EntityManagerFactory", e);
            throw new RuntimeException("Failed to initialize persistence system", e);
        }
    }
    
    public EntityManager getEntityManager() {
        EntityManager em = entityManager.get();
        if (em == null || !em.isOpen()) {
            em = entityManagerFactory.createEntityManager();
            entityManager.set(em);
            LOGGER.fine("New EntityManager created");
        }
        return em;
    }
    
    public void closeEntityManager() {
        EntityManager em = entityManager.get();
        if (em != null && em.isOpen()) {
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
                LOGGER.fine("EntityManager closed");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing EntityManager", e);
            } finally {
                entityManager.remove();
            }
        }
    }
    
    @PreDestroy
    public void cleanup() {
        closeEntityManager();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory.close();
                LOGGER.info("EntityManagerFactory closed");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error closing EntityManagerFactory", e);
            }
        }
        entityManagerFactory = null;
    }
}
