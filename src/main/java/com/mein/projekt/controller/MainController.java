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

@Named
@SessionScoped
public class MainController implements Serializable {

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

    @PostConstruct
    public void init() {
        this.countries = shop.getCountries();
    }

    // Login-Logik
    public String login() {
        currentUser.reset();
        currentUser.handleUser(userInput, passwordInput);

        if (!currentUser.isValid()) {
            failureMessage = "Login fehlgeschlagen!";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, failureMessage, ""));
            return "";
        }

        failureMessage = "";
        return currentUser.getUser().isAdmin() ? "backoffice.xhtml?faces-redirect=true" : "shopclient.xhtml?faces-redirect=true";
    }

    // CO₂-Daten speichern
    public void saveCO2Data() {
        Artikel newArtikel = new Artikel(landInput, jahrInput, co2AusstossInput, einheitInput, beschreibungInput);
        shop.handleArtikel(newArtikel, currentUser);
        
        // Felder zurücksetzen
        landInput = null;
        jahrInput = 0;
        co2AusstossInput = 0.0;
        einheitInput = null;
        beschreibungInput = null;
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "CO₂-Daten wurden erfolgreich gespeichert."));
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
}
