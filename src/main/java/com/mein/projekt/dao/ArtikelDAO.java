package com.mein.projekt.dao;

import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collections;
import java.util.List;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.auth.Shop;

@Named
@ApplicationScoped
public class ArtikelDAO {

    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;


    @Inject
    public ArtikelDAO(EntityManagerProvider entityManagerProvider) {
        try {
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

    public List<String> getAllCountries() {
        try {
            return entityManager.createQuery(
                            "SELECT DISTINCT a.name FROM Artikel a", String.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String findLatestBeschreibungByCountry(String selectedCountry) {
        try {
            return entityManager.createQuery(
                            "SELECT a.beschreibung FROM Artikel a WHERE a.name = :name ORDER BY a.verfuegbarAb DESC", String.class)
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
        ArtikelDAO dao = new ArtikelDAO(provider);
        List<Artikel> artikelListe = dao.entityManager.createQuery("SELECT a FROM Artikel a", Artikel.class).getResultList();
        for (Artikel art : artikelListe) {
            System.out.println(art.getText());
        }
        System.err.println("Es gibt " + dao.getArtikelCount() + " CO₂-Datensätze.");
    }

    public void saveArtikel(Artikel artikel1) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            entityManager.persist(artikel1);
            tx.commit();
            System.out.println("Artikel erfolgreich gespeichert: " + artikel1.getName());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern des Artikels.");
        }
    }
}
