package com.mein.projekt.dao;

import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.auth.Shop;

@Named
@ApplicationScoped
public class ArtikelDAO {

    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;


    public ArtikelDAO() {
        try {
            EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
            // Verwende die neue Persistence Unit "likeHeroPU"
            entityManager = entityManagerProvider.getEntityManager();
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
        ArtikelDAO dao = new ArtikelDAO();
        List<Artikel> artikelListe = dao.entityManager.createQuery("SELECT a FROM Artikel a", Artikel.class).getResultList();
        for (Artikel art : artikelListe) {
            System.out.println(art.getText());
        }
        System.err.println("Es gibt " + dao.getArtikelCount() + " CO₂-Datensätze.");
    }
}
