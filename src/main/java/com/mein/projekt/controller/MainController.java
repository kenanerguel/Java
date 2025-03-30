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

    private String nameInput;
    private String beschreibungInput;
    private String bildInput;
    private Date verfuegbarInput;

    private String userInput;
    private String passwordInput;
    private String failureMessage = "";

    private int index = 0;
    private Artikel artikel;

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

    // Artikel speichern
    public void saveArtikel() {
        Artikel newArtikel = new Artikel(nameInput, beschreibungInput, bildInput, verfuegbarInput);
        shop.handleArtikel(newArtikel, currentUser);
        this.countries = shop.getCountries();
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


    // --- Getter & Setter ---

    public String getNameInput() {
        return nameInput;
    }

    public void setNameInput(String nameInput) {
        this.nameInput = nameInput;
    }

    public String getBeschreibungInput() {
        return beschreibungInput;
    }

    public void setBeschreibungInput(String beschreibungInput) {
        this.beschreibungInput = beschreibungInput;
    }

    public String getBildInput() {
        return bildInput;
    }

    public void setBildInput(String bildInput) {
        this.bildInput = bildInput;
    }

    public Date getVerfuegbarInput() {
        return verfuegbarInput;
    }

    public void setVerfuegbarInput(Date verfuegbarInput) {
        this.verfuegbarInput = verfuegbarInput;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public double getLatestCO2() {
        return latestCO2;
    }

    public int getLatestYear() {
        return latestYear;
    }

    public void setLatestCO2(double latestCO2) {
        this.latestCO2 = latestCO2;
    }

    public void setLatestYear(int latestYear) {
        this.latestCO2 = latestYear;
    }
    // Getter und Setter
    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public List<String> getCountries() {
        return countries;
    }
}
