package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProvider {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("likeHeroPU");
    private static final EntityManager ENTITY_MANAGER = ENTITY_MANAGER_FACTORY.createEntityManager();

    public EntityManager getEntityManager() {
        return ENTITY_MANAGER;
    }
}
