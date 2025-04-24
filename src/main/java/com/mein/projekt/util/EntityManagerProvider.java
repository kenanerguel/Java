package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.Serializable;

@Named
@ApplicationScoped
public class EntityManagerProvider implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf;
    private EntityManager em;

    public EntityManagerProvider() {
        try {
            emf = Persistence.createEntityManagerFactory("likeHeroPU");
            em = emf.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Produces
    @ApplicationScoped
    public EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        return em;
    }

    public void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    public void closeEntityManagerFactory() {
        closeEntityManager();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
