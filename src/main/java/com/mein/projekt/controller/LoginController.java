package com.mein.projekt.controller;

import com.mein.projekt.auth.CurrentUser;
import com.mein.projekt.auth.Shop;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class LoginController implements Serializable {

    @Inject
    private Shop shop;

    @Inject
    private CurrentUser currentUser;

    // Der Salt-Wert sollte idealerweise aus einer sicheren Konfiguration kommen.
    // Für diesen Prototyp nutzen wir einen statischen Wert.
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    // Eingabefelder für Login (Benutzername und Passwort)
    private String user;
    private String password;

    // Zwischenvariable, um den Benutzernamen während der Validierung zwischenzuspeichern
    private String tempUsername;

    // Fehlermeldung, die bei ungültigen Login-Daten angezeigt wird
    private String failureMessage = "";

    // Getter und Setter
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    /**
     * Prüft zu Beginn, ob der Benutzer eingeloggt ist.
     * Falls nicht, wird zur Login-Seite navigiert.
     */

    /*
    public void checkLogin() {
        if (!currentUser.isValid()) {
            failureMessage = "Bitte loggen Sie sich ein.";
            FacesContext fc = FacesContext.getCurrentInstance();
            NavigationHandler nh = fc.getApplication().getNavigationHandler();
            nh.handleNavigation(fc, null, "login.xhtml?faces-redirect=true");
        }
    }
    */

    /**
     * Meldet den Benutzer ab und leitet zur Login-Seite weiter.
     */

    /*
    public String logout() {
        currentUser.reset();
        return "login.xhtml?faces-redirect=true";
    }
    */

    /**
     * Speichert den Benutzernamen nach der Post-Validierung eines Eingabefelds.
     */
    public void postValidateUser(ComponentSystemEvent ev) {
        UIInput temp = (UIInput) ev.getComponent();
        this.tempUsername = (String) temp.getValue();
    }

    /**
     * Validiert das eingegebene Passwort.
     * Wenn der Login fehlschlägt, wird eine Fehlermeldung erzeugt.
     */
    /*
    public void validateLogin(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String passwordValue = (String) value;
        shop.validateUsernameAndPassword(currentUser, tempUsername, passwordValue, salt);
        if (!currentUser.isValid()) {
            throw new ValidatorException(new FacesMessage("Login falsch!"));
        }
    }
    */

    /**
     * Versucht, den Benutzer einzuloggen.
     * Bei Erfolg wird entsprechend der Benutzerrolle weitergeleitet.
     * Admins werden ins Backoffice geleitet, während andere (z. B. Daten beitragende Wissenschaftler:innen)
     * in eine Client-Ansicht (z. B. Shop-Client) weitergeleitet werden.
     */
    public String login() {
        currentUser.handleUser(user, password);
        if (currentUser.getIsAdmin()) {
            failureMessage = "";
            return "backoffice.xhtml?faces-redirect=true";
        } else {
            failureMessage = "";
            return "shopclient.xhtml?faces-redirect=true";
        }
    }

    // Eine Main-Methode zum Testen der Hash-Funktion (optional)
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java LoginController user pass");
            System.exit(1);
        }
        System.out.println(Shop.hashPassword(args[0], args[1], salt));
    }
}
