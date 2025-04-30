package com.mein.projekt.controller;

import com.mein.projekt.auth.CurrentUser;
import com.mein.projekt.auth.Shop;
import com.mein.projekt.model.Artikel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@SessionScoped
public class MainController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @Inject
    private Shop shop;

    @Inject
    private CurrentUser currentUser;

    // Neue Eingabefelder für CO₂-Daten
    private String landInput;
    private int jahrInput;
    private double co2AusstossInput;
    private String einheitInput;
    private String beschreibungInput;

    private String userInput;
    private String passwordInput;
    private String failureMessage = "";

    private String selectedCountry;
    private double latestCO2;
    private int latestYear;
    private List<String> countries;
    
    // Neue Felder für Artikel-Verwaltung
    private Artikel selectedArtikel;
    private String rejectionReason;

    private List<Artikel> pendingArticles;

    @PostConstruct
    public void init() {
        this.countries = shop.getCountries();
        loadPendingArticles();
    }

    // Login-Logik
    public String login() {
        try {
            LOGGER.info("=== Login-Versuch Start ===");
            LOGGER.info("Session ID: " + FacesContext.getCurrentInstance().getExternalContext().getSession(true));
            LOGGER.info("Benutzer: " + userInput);
            LOGGER.info("CurrentUser Objekt: " + (currentUser != null ? "vorhanden" : "null"));
            LOGGER.info("Shop Objekt: " + (shop != null ? "vorhanden" : "null"));
            
            // Neue Debug-Informationen
            LOGGER.info("Server Info: " + FacesContext.getCurrentInstance().getExternalContext().getRequestServerName());
            LOGGER.info("Client Info: " + FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("User-Agent"));
            
            if (currentUser == null) {
                LOGGER.severe("CurrentUser ist null - CDI Injection fehlgeschlagen");
                LOGGER.severe("Bean Manager Status: " + checkBeanManagerStatus());
                failureMessage = "Systemfehler: Bitte kontaktieren Sie den Administrator";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", failureMessage));
                return "";
            }
            
            currentUser.reset();
            LOGGER.info("CurrentUser nach Reset: " + (currentUser.getUser() != null ? "hat User" : "kein User"));
            
            LOGGER.info("Versuche Benutzer zu authentifizieren...");
            try {
                currentUser.handleUser(userInput, passwordInput);
                LOGGER.info("Authentifizierung durchgeführt");
            } catch (Exception e) {
                LOGGER.severe("Fehler bei der Authentifizierung: " + e.getMessage());
                throw e;
            }
            LOGGER.info("CurrentUser nach handleUser: " + (currentUser.getUser() != null ? "hat User" : "kein User"));

            if (!currentUser.isValid()) {
                failureMessage = "Login fehlgeschlagen: Benutzername oder Passwort falsch";
                LOGGER.warning("Login fehlgeschlagen für Benutzer: " + userInput);
                LOGGER.warning("CurrentUser ist nicht gültig: " + (currentUser != null ? currentUser.getUser() : "null"));
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", failureMessage));
                return "";
            }

            failureMessage = "";
            LOGGER.info("Login erfolgreich für Benutzer: " + userInput + ", isAdmin: " + currentUser.getUser().isAdmin());
            String redirectUrl = currentUser.getUser().isAdmin() ? 
                "admin/pending.xhtml?faces-redirect=true" : 
                "myarticles.xhtml?faces-redirect=true";
            LOGGER.info("Weiterleitung zu: " + redirectUrl);
            return redirectUrl;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ein unerwarteter Fehler ist aufgetreten beim Login für Benutzer: " + userInput, e);
            failureMessage = "Login fehlgeschlagen: " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", failureMessage));
            return "";
        } finally {
            LOGGER.info("=== Login-Versuch Ende ===");
        }
    }

    private String checkBeanManagerStatus() {
        try {
            jakarta.enterprise.inject.spi.BeanManager beanManager = 
                jakarta.enterprise.inject.spi.CDI.current().getBeanManager();
            return beanManager != null ? "BeanManager verfügbar" : "BeanManager nicht verfügbar";
        } catch (Exception e) {
            return "BeanManager Status konnte nicht ermittelt werden: " + e.getMessage();
        }
    }

    // CO₂-Daten speichern
    public String saveCo2Data() {
        try {
            LOGGER.info("=== Speichere neue CO₂-Daten ===");
            LOGGER.info("Land: " + landInput);
            LOGGER.info("Jahr: " + jahrInput);
            LOGGER.info("CO₂-Ausstoß: " + co2AusstossInput);
            LOGGER.info("Einheit: " + einheitInput);
            LOGGER.info("Beschreibung: " + beschreibungInput);
            LOGGER.info("Benutzer: " + (currentUser != null ? currentUser.getUser().getUsername() : "null"));
            
            // Validierung der Eingaben
            if (landInput == null || landInput.trim().isEmpty()) {
                throw new IllegalArgumentException("Land ist erforderlich");
            }
            if (jahrInput < 1900 || jahrInput > 2100) {
                throw new IllegalArgumentException("Jahr muss zwischen 1900 und 2100 liegen");
            }
            if (co2AusstossInput < 0) {
                throw new IllegalArgumentException("CO₂-Ausstoß muss positiv sein");
            }
            if (einheitInput == null || einheitInput.trim().isEmpty()) {
                throw new IllegalArgumentException("Einheit ist erforderlich");
            }
            if (beschreibungInput == null || beschreibungInput.trim().isEmpty()) {
                throw new IllegalArgumentException("Beschreibung ist erforderlich");
            }
            
            // Erstelle neuen Artikel
            Artikel artikel = new Artikel();
            artikel.setLand(landInput.trim());
            artikel.setJahr(jahrInput);
            artikel.setCo2Ausstoss(co2AusstossInput);
            artikel.setEinheit(einheitInput.trim());
            artikel.setBeschreibung(beschreibungInput.trim());
            artikel.setUser(currentUser.getUser());
            
            // Setze Zeitstempel und Status
            Date now = new Date();
            artikel.setCreatedAt(now);
            artikel.setUpdatedAt(now);
            artikel.setCreatedBy(currentUser.getUser());
            artikel.setUpdatedBy(currentUser.getUser());
            artikel.setStatus("pending");
            artikel.setErstelltAm(now);
            
            // Speichere den Artikel
            shop.handleArtikel(artikel, currentUser.getUser());
            
            LOGGER.info("Artikel erfolgreich gespeichert mit ID: " + artikel.getId());
            
            // Zeige Erfolgsmeldung
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", 
                    "CO₂-Daten wurden erfolgreich gespeichert und warten auf Freigabe."));
            
            // Felder zurücksetzen
            landInput = null;
            jahrInput = 0;
            co2AusstossInput = 0.0;
            einheitInput = null;
            beschreibungInput = null;
            
            // Zurück zur Übersicht
            return "myarticles.xhtml?faces-redirect=true";
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern der CO₂-Daten", e);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", 
                    "Fehler beim Speichern der CO₂-Daten: " + e.getMessage()));
            return null;
        } finally {
            LOGGER.info("=== Ende CO₂-Daten speichern ===");
        }
    }

    // Neue Methode für Admin-Genehmigungen
    public void handleApproval(Artikel artikel, boolean approved) {
        artikel.setStatus(approved ? "approved" : "rejected");
        shop.updateArtikel(artikel);
        
        String message = approved ? 
            "Artikel wurde genehmigt." :
            "Artikel wurde abgelehnt.";
            
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", message));
    }
    
    // Neue Methode für Ablehnungen mit Begründung
    public void handleRejection() {
        if (selectedArtikel != null) {
            selectedArtikel.setStatus("rejected");
            selectedArtikel.setBeschreibung(selectedArtikel.getBeschreibung() + 
                "\n\nAbgelehnt mit Begründung: " + rejectionReason);
            shop.updateArtikel(selectedArtikel);
            
            // Felder zurücksetzen
            selectedArtikel = null;
            rejectionReason = null;
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Artikel wurde abgelehnt."));
        }
    }
    
    // Methode um ausstehende Artikel zu laden
    public List<Artikel> getPendingArtikel() {
        return shop.getPendingArtikel();
    }
    
    // Methode um Artikel eines Wissenschaftlers zu laden
    public List<Artikel> getMyArtikel() {
        return shop.getArtikelByUser(currentUser.getUser());
    }

    public void onCountryChange() {
        List<Number> result = shop.handleLatestValues(selectedCountry);
        if (result != null && result.size() == 2) {
            latestCO2 = (double) result.get(0);
            latestYear = (int) result.get(1);
        } else {
            latestCO2 = -1;
            latestYear = -1;
        }
    }

    // Getter & Setter für die neuen Felder
    public String getLandInput() { return landInput; }
    public void setLandInput(String landInput) { this.landInput = landInput; }
    
    public int getJahrInput() { return jahrInput; }
    public void setJahrInput(int jahrInput) { this.jahrInput = jahrInput; }
    
    public double getCo2AusstossInput() { return co2AusstossInput; }
    public void setCo2AusstossInput(double co2AusstossInput) { this.co2AusstossInput = co2AusstossInput; }
    
    public String getEinheitInput() { return einheitInput; }
    public void setEinheitInput(String einheitInput) { this.einheitInput = einheitInput; }
    
    public String getBeschreibungInput() { return beschreibungInput; }
    public void setBeschreibungInput(String beschreibungInput) { this.beschreibungInput = beschreibungInput; }

    // Bestehende Getter & Setter
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    
    public String getPasswordInput() { return passwordInput; }
    public void setPasswordInput(String passwordInput) { this.passwordInput = passwordInput; }
    
    public String getFailureMessage() { return failureMessage; }
    public void setFailureMessage(String failureMessage) { this.failureMessage = failureMessage; }
    
    public double getLatestCO2() { return latestCO2; }
    public void setLatestCO2(double latestCO2) { this.latestCO2 = latestCO2; }
    
    public int getLatestYear() { return latestYear; }
    public void setLatestYear(int latestYear) { this.latestYear = latestYear; }
    
    public String getSelectedCountry() { return selectedCountry; }
    public void setSelectedCountry(String selectedCountry) { this.selectedCountry = selectedCountry; }
    
    public List<String> getCountries() { return countries; }
    public List<String> getAvailableCountries() { return Arrays.asList("Deutschland", "Frankreich", "USA", "China", "Japan", "Indien", "Brasilien", "Russland"); }
    
    // Neue Getter & Setter
    public Artikel getSelectedArtikel() { return selectedArtikel; }
    public void setSelectedArtikel(Artikel selectedArtikel) { this.selectedArtikel = selectedArtikel; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public List<Artikel> getPendingArticles() {
        return pendingArticles;
    }
    
    private void loadPendingArticles() {
        try {
            pendingArticles = shop.getPendingArtikel();
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Laden der ausstehenden Artikel");
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", 
                "Fehler beim Laden der ausstehenden Artikel"));
        }
    }
    
    public void approveArticle(Artikel artikel) {
        try {
            LOGGER.info("Genehmige Artikel: " + artikel.getId());
            artikel.setStatus("approved");
            artikel.setUpdatedBy(currentUser.getUser());
            shop.handleArtikel(artikel, currentUser.getUser());
            loadPendingArticles();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", 
                "Artikel wurde genehmigt"));
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Genehmigen des Artikels");
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", 
                "Fehler beim Genehmigen des Artikels"));
        }
    }
    
    public void rejectArticle(Artikel artikel) {
        try {
            LOGGER.info("Lehne Artikel ab: " + artikel.getId());
            artikel.setStatus("rejected");
            artikel.setUpdatedBy(currentUser.getUser());
            shop.handleArtikel(artikel, currentUser.getUser());
            loadPendingArticles();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", 
                "Artikel wurde abgelehnt"));
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Ablehnen des Artikels");
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", 
                "Fehler beim Ablehnen des Artikels"));
        }
    }
}
