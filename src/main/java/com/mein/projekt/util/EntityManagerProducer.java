package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProducer {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("likeHeroPU");
    
    @Produces
    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
} 