package com.mein.projekt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;
import com.mein.projekt.model.Artikel;

@Path("/artikel")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtikelController {

    private static final Logger LOGGER = Logger.getLogger(ArtikelController.class.getName());

    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("/latest/{land}")
    @Transactional
    public Response getLatestByLand(@PathParam("land") String land) {
        try {
            LOGGER.info("Suche nach neuesten Daten für Land: " + land);
            TypedQuery<Artikel> query = em.createNamedQuery("Artikel.findLatestByLand", Artikel.class);
            query.setParameter("land", land);
            query.setMaxResults(1);
            
            List<Artikel> results = query.getResultList();
            if (!results.isEmpty()) {
                Artikel artikel = results.get(0);
                LOGGER.info("Gefunden: Land=" + artikel.getLand() + ", Jahr=" + artikel.getJahr() + 
                           ", CO2=" + artikel.getCo2Ausstoss() + " " + artikel.getEinheit());
                return Response.ok(artikel).build();
            } else {
                LOGGER.warning("Keine Daten gefunden für Land: " + land);
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("Keine Daten für das Land " + land + " gefunden")
                             .build();
            }
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Abrufen der Daten: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Fehler beim Abrufen der Daten: " + e.getMessage())
                         .build();
        }
    }

    @PUT
    @Path("/update/{land}/{jahr}")
    @Transactional
    public Response updateCo2Ausstoss(@PathParam("land") String land, 
                                     @PathParam("jahr") int jahr, 
                                     Artikel updatedArtikel) {
        try {
            LOGGER.info("Aktualisiere Daten für Land: " + land + ", Jahr: " + jahr);
            LOGGER.info("Neue Daten: CO2=" + updatedArtikel.getCo2Ausstoss() + 
                       " " + updatedArtikel.getEinheit() + ", Beschreibung: " + updatedArtikel.getBeschreibung());
            
            // Suche nach dem Artikel mit dem angegebenen Land und Jahr
            TypedQuery<Artikel> query = em.createQuery(
                "SELECT a FROM Artikel a WHERE a.land = :land AND a.jahr = :jahr", 
                Artikel.class);
            query.setParameter("land", land);
            query.setParameter("jahr", jahr);
            
            List<Artikel> results = query.getResultList();
            
            if (!results.isEmpty()) {
                Artikel artikel = results.get(0);
                LOGGER.info("Gefunden: Land=" + artikel.getLand() + ", Jahr=" + artikel.getJahr() + 
                           ", CO2=" + artikel.getCo2Ausstoss() + " " + artikel.getEinheit());
                
                // Aktualisiere nur den CO₂-Ausstoß und die Einheit
                artikel.setCo2Ausstoss(updatedArtikel.getCo2Ausstoss());
                artikel.setEinheit(updatedArtikel.getEinheit());
                artikel.setBeschreibung(updatedArtikel.getBeschreibung());
                
                // Speichere die Änderungen
                em.merge(artikel);
                
                LOGGER.info("Daten aktualisiert: Land=" + artikel.getLand() + ", Jahr=" + artikel.getJahr() + 
                           ", CO2=" + artikel.getCo2Ausstoss() + " " + artikel.getEinheit());
                
                return Response.ok(artikel).build();
            } else {
                LOGGER.info("Kein Artikel gefunden, erstelle einen neuen");
                // Wenn kein Artikel gefunden wurde, erstelle einen neuen
                Artikel newArtikel = new Artikel();
                newArtikel.setLand(land);
                newArtikel.setJahr(jahr);
                newArtikel.setCo2Ausstoss(updatedArtikel.getCo2Ausstoss());
                newArtikel.setEinheit(updatedArtikel.getEinheit());
                newArtikel.setBeschreibung(updatedArtikel.getBeschreibung());
                newArtikel.setStatus("approved");
                
                em.persist(newArtikel);
                
                LOGGER.info("Neuer Artikel erstellt: Land=" + newArtikel.getLand() + ", Jahr=" + newArtikel.getJahr() + 
                           ", CO2=" + newArtikel.getCo2Ausstoss() + " " + newArtikel.getEinheit());
                
                return Response.ok(newArtikel).build();
            }
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Aktualisieren der Daten: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Fehler beim Aktualisieren der Daten: " + e.getMessage())
                         .build();
        }
    }
} 