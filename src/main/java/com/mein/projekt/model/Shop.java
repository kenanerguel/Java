package com.mein.projekt.model;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class Shop {
    @Inject
    private EntityManager em;

    public List<Artikel> getPendingArtikel() {
        TypedQuery<Artikel> query = em.createQuery(
            "SELECT a FROM Artikel a WHERE a.status = 'pending' ORDER BY a.createdAt DESC", 
            Artikel.class
        );
        return query.getResultList();
    }
    
    public void handleArtikel(Artikel artikel, User user) {
        artikel.setUpdatedAt(new Date());
        artikel.setUpdatedBy(user);
        em.merge(artikel);
    }

    public void handleArtikel(Artikel artikel) {
        artikel.setUpdatedAt(new Date());
        em.merge(artikel);
    }
} 