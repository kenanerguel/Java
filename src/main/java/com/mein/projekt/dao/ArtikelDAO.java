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
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

import com.mein.projekt.model.Artikel;
import com.mein.projekt.auth.Shop;

@Named
@ApplicationScoped
public class ArtikelDAO {

    private final EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;


    @Inject
    public ArtikelDAO(EntityManagerProvider entityManagerProvider) {
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
        ArtikelDAO dao = new ArtikelDAO(provider);
        List<Artikel> artikelListe = dao.entityManager.createQuery("SELECT a FROM Artikel a", Artikel.class).getResultList();
        for (Artikel art : artikelListe) {
            System.out.println(art.getBeschreibung());
        }
        System.err.println("Es gibt " + dao.getArtikelCount() + " CO₂-Datensätze.");
    }

    public void saveArtikel(Artikel artikel) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(artikel);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
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
}
